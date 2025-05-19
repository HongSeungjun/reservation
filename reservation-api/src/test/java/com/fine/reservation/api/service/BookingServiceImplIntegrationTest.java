package com.fine.reservation.api.service;

import com.fine.reservation.api.dto.BookingRequest;
import com.fine.reservation.api.service.notification.NotificationService;
import com.fine.reservation.api.service.notification.PushNotificationService;
import com.fine.reservation.api.service.notification.RedisCacheService;
import com.fine.reservation.api.service.notification.WebSocketService;
import com.fine.reservation.domain.booking.model.Booking;
import com.fine.reservation.domain.booking.repository.BookingRepository;
import com.fine.reservation.domain.enums.ReservationStatus;
import com.fine.reservation.domain.reservation.model.Reservation;
import com.fine.reservation.domain.reservation.repository.ReservationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Transactional
@ActiveProfiles("test")
class BookingServiceImplIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @MockitoBean
    private NotificationService notificationService;

    @MockitoBean
    private PushNotificationService pushNotificationService;

    @MockitoBean
    private WebSocketService webSocketService;

    @MockitoBean
    private RedisCacheService redisCacheService;

    @Test
    @DisplayName("예약 승인 후 여러 Booking 생성 및 알림 호출 정상 흐름")
    void createBookings_withReservation_success() {
        // given
        Reservation reservation = Reservation.builder()
                .reserveStatus(ReservationStatus.REQUEST)
                .build();
        Reservation savedReservation = reservationRepository.save(reservation);

        BookingRequest request = BookingRequest.builder()
                .reserveNo(savedReservation.getReserveNo())
                .bookingStartAt(LocalDateTime.now().toString())
                .bookingEndAt(LocalDateTime.now().plusHours(1).toString())
                .machineNos(new Long[]{101L, 102L})
                .build();

        // when
        List<Long> bookingNos = bookingService.createBookings(request);

        // then
        assertThat(bookingNos).hasSize(2);

        List<Booking> savedBookings = bookingRepository.findAll();
        assertThat(savedBookings).hasSize(2);

        Reservation updatedReservation = reservationRepository.findById(savedReservation.getReserveNo()) // ✅ savedReservation
                .orElseThrow();
        assertThat(updatedReservation.getReserveStatus()).isEqualTo(ReservationStatus.APPROVAL);

        verify(notificationService, times(2)).sendBookingConfirmation(any(Booking.class));
        verify(pushNotificationService, times(2)).sendBookingNotification(any(Booking.class));
        verify(webSocketService, times(2)).broadcastBookingUpdate(any(Booking.class));
        verify(redisCacheService, times(1)).updateBookingCache(anyList());
    }

    @Test
    @DisplayName("예약번호가 잘못되면 예외 발생")
    void createBookings_withInvalidReservation_throwsException() {
        // given
        BookingRequest request = BookingRequest.builder()
                .reserveNo(99999L) // 존재하지 않는 예약 번호
                .bookingStartAt(LocalDateTime.now().toString())
                .bookingEndAt(LocalDateTime.now().plusHours(1).toString())
                .machineNos(new Long[]{101L})
                .build();

        // when / then
        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> {
            bookingService.createBookings(request);
        });

        verifyNoInteractions(notificationService, pushNotificationService, webSocketService, redisCacheService);
    }
}