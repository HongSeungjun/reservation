package com.fine.reservation.api.service;

import com.fine.reservation.api.dto.BookingRequest;
import com.fine.reservation.api.mapper.BookingDtoMapper;
import com.fine.reservation.api.service.notification.NotificationService;
import com.fine.reservation.api.service.notification.PushNotificationService;
import com.fine.reservation.api.service.notification.RedisCacheService;
import com.fine.reservation.api.service.notification.WebSocketService;
import com.fine.reservation.domain.booking.entity.BookingEntity;
import com.fine.reservation.domain.booking.repository.BookingRepository;
import com.fine.reservation.domain.enums.ReservationStatus;
import com.fine.reservation.domain.reservation.entity.ReservationEntity;
import com.fine.reservation.domain.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    // reservation repository를 가져와서 써도 되는지
    private final ReservationRepository reservationRepository;

    private final NotificationService notificationService;
    private final PushNotificationService pushService;
    private final WebSocketService webSocketService;
    private final RedisCacheService cacheService;

    @Transactional
    public List<Long> createBookings(BookingRequest request) {
        if (request.reserveNo() != null) {
            approveReservation(request.reserveNo());
        }

        List<BookingEntity> createdBookings = createMultipleBookings(request);

        notifyBookingCreation(createdBookings);

        return createdBookings.stream().map(BookingEntity::getBookingNo).toList();

    }

    @Transactional(readOnly = true)
    public List<BookingEntity> getBookingsByDateRange(LocalDate startDate, LocalDate endDate) {
        LocalDateTime queryStartDateTime = startDate.atStartOfDay();
        LocalDateTime queryEndDateTime = endDate.atTime(LocalTime.MAX);

        return bookingRepository.findByBookingStartAtBetween(queryStartDateTime, queryEndDateTime);
    }

    @Transactional
    public List<Long> updateBookings(Long bookingNo, BookingRequest request) {
        for (Integer machineNo : request.machineNos()) {
            boolean overlap = bookingRepository.existsOverlap(machineNo, request.bookingStartAt(), request.bookingEndAt(), bookingNo);
            if (overlap) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "중복 예약이 존재합니다: machineNo=" + machineNo);
            }
        }

        List<Long> updatedBookingNos;
        BookingEntity originalEntity = bookingRepository.findById(bookingNo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "예약을 찾을 수 없습니다: " + bookingNo));

        if (request.machineNos().size() == 1) {
            BookingEntity updatedEntity = buildUpdatedBookingEntity(originalEntity, request);
            BookingEntity savedEntity = bookingRepository.save(updatedEntity);
            updatedBookingNos = List.of(savedEntity.getBookingNo());

        } else {
            deleteForUpdate(bookingNo, request);
            List<BookingEntity> createdBookings = createMultipleBookings(request);
            updatedBookingNos = createdBookings.stream().map(BookingEntity::getBookingNo).toList();
        }

        if (!originalEntity.getBookingStartAt().isEqual(request.bookingStartAt())
                || !originalEntity.getBookingEndAt().isEqual(request.bookingEndAt())) {
            log.info("Booking time changed for bookingNo: {}. Triggering notifications.", bookingNo);
            // TODO : 예약시간 변경시에만 예약 시간 알림톡, 웹소켓 등 외부 API 호출
        }

        return updatedBookingNos;
    }

    private void approveReservation(Long reservationNo) {
        ReservationEntity reservation = reservationRepository.findById(reservationNo).orElseThrow(() -> new RuntimeException("예약 정보를 찾을 수 없습니다: " + reservationNo));

        if (reservation.getReserveStatus() != ReservationStatus.REQUEST) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ("유효하지 않은 예약 상태입니다: " + reservation.getReserveStatus()));
        }

        // 상태 업데이트 어떻게 하는것이 좋은지
        ReservationEntity approvedReservation = reservation.approve();
        reservationRepository.save(approvedReservation);
    }

    private List<BookingEntity> createMultipleBookings(BookingRequest request) {
        List<BookingEntity> bookings = new ArrayList<>();

        for (Integer machineNo : request.machineNos()) {
            BookingEntity booking = createSingleBooking(request, machineNo);
            bookings.add(bookingRepository.save(booking));
        }

        return bookings;
    }

    private BookingEntity createSingleBooking(BookingRequest request, Integer machineNo) {

        return BookingEntity.builder()
                .shopNo(request.shopNo())
                .machineNo(machineNo)
                .bookingStartAt(request.bookingStartAt())
                .bookingEndAt(request.bookingEndAt())
                .peopleCount(request.peopleCount())
                .holeCount(request.holeCount())
                .bookerName(request.bookerName())
                .phoneNumber(request.phoneNumber())
                .bookingChannel(request.bookingChannel())
                .gameMode(request.gameMode())
                .gameDurationMinutes(request.gameDurationMinutes())
                .bookingMemo(request.bookingMemo())
                .reserveNo(request.reserveNo())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

    }

    private void notifyBookingCreation(List<BookingEntity> bookings) {
        for (BookingEntity booking : bookings) {
            notificationService.sendBookingConfirmation(booking);
            pushService.sendBookingNotification(booking);
            webSocketService.broadcastBookingUpdate(booking);
        }
        cacheService.updateBookingCache(bookings);
    }


    private void deleteForUpdate(Long bookingNo, BookingRequest request) {
        if (request.reserveNo() == null) {
            int result = bookingRepository.deleteByBookingNoAndShopNo(bookingNo, request.shopNo());
            if (result < 1) {
                throw new IllegalStateException("booking delete fail");
            }
        } else {
            int result = bookingRepository.deleteByReserveNoAndShopNo(request.reserveNo(), request.shopNo());
            if (result < 1) {
                throw new IllegalStateException("reserve booking delete fail");
            }
        }
    }

    private BookingEntity buildUpdatedBookingEntity(BookingEntity originalEntity, BookingRequest request) {
        Integer machineToUpdate = request.machineNos().get(0);

        return BookingEntity.builder()
                .bookingNo(originalEntity.getBookingNo())
                .createdAt(originalEntity.getCreatedAt())

                .shopNo(request.shopNo())
                .machineNo(machineToUpdate)
                .reserveNo(request.reserveNo())
                .bookingStartAt(request.bookingStartAt())
                .bookingEndAt(request.bookingEndAt())
                .peopleCount(request.peopleCount())
                .holeCount(request.holeCount())
                .bookerName(request.bookerName())
                .phoneNumber(request.phoneNumber())
                .bookingMemo(request.bookingMemo())
                .bookingChannel(request.bookingChannel())
                .gameMode(request.gameMode())
                .gameDurationMinutes(request.gameDurationMinutes())

                .updatedAt(LocalDateTime.now())
                .build();
    }

}
