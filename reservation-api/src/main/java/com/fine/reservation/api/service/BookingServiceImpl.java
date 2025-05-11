package com.fine.reservation.api.service;

import com.fine.reservation.api.dto.BookingRequest;
import com.fine.reservation.api.service.notification.NotificationService;
import com.fine.reservation.api.service.notification.PushNotificationService;
import com.fine.reservation.api.service.notification.RedisCacheService;
import com.fine.reservation.api.service.notification.WebSocketService;
import com.fine.reservation.domain.booking.model.Booking;
import com.fine.reservation.domain.booking.repository.BookingRepository;
import com.fine.reservation.domain.enums.GameMode;
import com.fine.reservation.domain.enums.ReservationStatus;
import com.fine.reservation.domain.reservation.model.Reservation;
import com.fine.reservation.domain.reservation.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ReservationRepository reservationRepository;

    private final NotificationService notificationService;
    private final PushNotificationService pushService;
    private final WebSocketService webSocketService;
    private final RedisCacheService cacheService;

    @Override
    @Transactional
    public List<Long> createBookings(BookingRequest request) {
        if (request.getReserveNo() != null) {
            updateReservationStatus(request.getReserveNo());
        }

        List<Booking> createdBookings = createMultipleBookings(request);

        notifyBookingCreation(createdBookings);

        return createdBookings.stream()
                .map(Booking::getBookingNo)
                .toList();

    }


    private void updateReservationStatus(Long reservationNo) {
        Reservation reservation = reservationRepository.findById(reservationNo)
                .orElseThrow(() -> new RuntimeException("예약 정보를 찾을 수 없습니다: " + reservationNo));

        if (reservation.getReserveStatus() != ReservationStatus.REQUEST) {
            throw new RuntimeException("유효하지 않은 예약 상태입니다: " + reservation.getReserveStatus());
        }

        Reservation approvedReservation = reservation.approve();
        reservationRepository.save(approvedReservation);
    }

    private List<Booking> createMultipleBookings(BookingRequest request) {
        List<Booking> bookings = new ArrayList<>();

        for (Long machineNo : request.getMachineNos()) {
            Booking booking = createSingleBooking(request, machineNo);
            bookings.add(bookingRepository.save(booking));
        }

        return bookings;
    }

    private Booking createSingleBooking(BookingRequest request, Long machineNo) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return Booking.builder()
                .machineNo(machineNo)
                .bookingStartAt(LocalDateTime.parse(request.getBookingStartAt(), formatter))
                .bookingEndAt(LocalDateTime.parse(request.getBookingEndAt(), formatter))
                .bookingPeople(request.getBookingPeople())
                .bookingPlayHole(request.getBookingPlayHole())
                .bookingName(request.getBookingName())
                .cellNumber(request.getCellNumber())
                .bookingChannel(request.getBookingChannel())
                .gameMode(GameMode.fromValue(request.getGameMode()))
                .gameTime(request.getGameTime())
                .bookingMemo(request.getBookingMemo())
                .reserveNo(request.getReserveNo())
                .reserveStatus(request.getReserveStatus())
                .build();

    }

    private void notifyBookingCreation(List<Booking> bookings) {
        for (Booking booking : bookings) {
            notificationService.sendBookingConfirmation(booking);
            pushService.sendBookingNotification(booking);
            webSocketService.broadcastBookingUpdate(booking);
        }

        cacheService.updateBookingCache(bookings);
    }
}
