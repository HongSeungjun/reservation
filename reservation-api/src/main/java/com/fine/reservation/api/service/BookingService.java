package com.fine.reservation.api.service;

import com.fine.reservation.api.dto.BookingRequest;
import com.fine.reservation.api.service.notification.NotificationService;
import com.fine.reservation.api.service.notification.PushNotificationService;
import com.fine.reservation.api.service.notification.RedisCacheService;
import com.fine.reservation.api.service.notification.WebSocketService;
import com.fine.reservation.domain.booking.entity.BookingEntity;
import com.fine.reservation.domain.booking.repository.BookingJpaRepository;
import com.fine.reservation.domain.enums.ReservationStatus;
import com.fine.reservation.domain.reservation.entity.ReservationEntity;
import com.fine.reservation.domain.reservation.repository.ReservationJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {
    private final BookingJpaRepository bookingJpaRepository;
    private final ReservationJpaRepository reservationRepository;

    private final NotificationService notificationService;
    private final PushNotificationService pushService;
    private final WebSocketService webSocketService;
    private final RedisCacheService cacheService;

    @Transactional
    public List<Long> createBookings(BookingRequest request) {
        if (request.reserveNo() != null) {
            updateReservationStatus(request.reserveNo());
        }

        List<BookingEntity> createdBookings = createMultipleBookings(request);

        notifyBookingCreation(createdBookings);

        return createdBookings.stream()
                .map(BookingEntity::getBookingNo)
                .toList();

    }

    @Transactional(readOnly = true)
    public List<BookingEntity> getBookingsByDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        return bookingJpaRepository.findByBookingStartAtBetween(startOfDay, endOfDay);
    }


    private void updateReservationStatus(Long reservationNo) {
        ReservationEntity reservation = reservationRepository.findById(reservationNo)
                .orElseThrow(() -> new RuntimeException("예약 정보를 찾을 수 없습니다: " + reservationNo));

        if (reservation.getReserveStatus() != ReservationStatus.REQUEST) {
            throw new RuntimeException("유효하지 않은 예약 상태입니다: " + reservation.getReserveStatus());
        }

        // 상태 업데이트 어떻게 하는것이 좋은지
        ReservationEntity approvedReservation = reservation.approve();
        reservationRepository.save(approvedReservation);
    }

    private List<BookingEntity> createMultipleBookings(BookingRequest request) {
        List<BookingEntity> bookings = new ArrayList<>();

        for (Integer machineNo : request.machineNos()) {
            BookingEntity booking = createSingleBooking(request, machineNo);
            bookings.add(bookingJpaRepository.save(booking));
        }

        return bookings;
    }

    private BookingEntity createSingleBooking(BookingRequest request, Integer machineNo) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return BookingEntity.builder()
                .shopNo(request.shopNo())
                .machineNo(machineNo)
                .bookingStartAt(LocalDateTime.parse(request.bookingStartAt(), formatter))
                .bookingEndAt(LocalDateTime.parse(request.bookingEndAt(), formatter))
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
}
