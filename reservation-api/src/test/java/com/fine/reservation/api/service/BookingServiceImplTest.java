package com.fine.reservation.api.service;

import com.fine.reservation.api.dto.BookingRequest;
import com.fine.reservation.api.service.notification.NotificationService;
import com.fine.reservation.api.service.notification.PushNotificationService;
import com.fine.reservation.api.service.notification.RedisCacheService;
import com.fine.reservation.api.service.notification.WebSocketService;
import com.fine.reservation.domain.booking.model.Booking;
import com.fine.reservation.domain.booking.repository.BookingRepository;
import com.fine.reservation.domain.enums.BookingChannel;
import com.fine.reservation.domain.enums.GameMode;
import com.fine.reservation.domain.enums.ReservationStatus;
import com.fine.reservation.domain.reservation.model.Reservation;
import com.fine.reservation.domain.reservation.repository.ReservationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private PushNotificationService pushService;

    @Mock
    private WebSocketService webSocketService;

    @Mock
    private RedisCacheService cacheService;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    @DisplayName("예약번호가 있는 경우 Reservation 상태를 업데이트하고 Booking을 생성한다")
    void createBookings_WithReservationNo_ShouldUpdateReservationStatusAndCreateBookings() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        String startDateTime = now.plusHours(1).toString();
        String endDateTime = now.plusHours(2).toString();

        BookingRequest request = BookingRequest.builder()
                .machineNos(new Long[]{1L, 2L})
                .bookingStartAt(startDateTime)
                .bookingEndAt(endDateTime)
                .bookingPeople(4)
                .bookingPlayHole(18)
                .bookingName("홍길동")
                .cellNumber("010-1234-5678")
                .bookingChannel(BookingChannel.MOBILE)
                .gameMode(GameMode.STROKE.getValue())
                .gameTime(120)
                .bookingMemo("모바일 예약")
                .reserveNo(1L)
                .build();

        Reservation reservation = mock(Reservation.class);
        when(reservation.getReserveStatus()).thenReturn(ReservationStatus.REQUEST);
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(reservation));

        Booking booking1 = Booking.builder()
                .bookingNo(101L)
                .machineNo(1L)
                .bookingStartAt(LocalDateTime.parse(startDateTime))
                .bookingEndAt(LocalDateTime.parse(endDateTime))
                .reserveNo(1L)
                .build();

        Booking booking2 = Booking.builder()
                .bookingNo(102L)
                .machineNo(2L)
                .bookingStartAt(LocalDateTime.parse(startDateTime))
                .bookingEndAt(LocalDateTime.parse(endDateTime))
                .reserveNo(1L)
                .build();

        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(booking1)
                .thenReturn(booking2);

        // When
        List<Long> result = bookingService.createBookings(request);

        // Then
        verify(reservationRepository).findById(1L);
        verify(reservation).approve();
        verify(reservationRepository).save(reservation);
        verify(bookingRepository, times(2)).save(any(Booking.class));
        verify(notificationService, times(2)).sendBookingConfirmation(any(Booking.class));
        verify(pushService, times(2)).sendBookingNotification(any(Booking.class));
        verify(webSocketService, times(2)).broadcastBookingUpdate(any(Booking.class));
        verify(cacheService).updateBookingCache(anyList());

        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(101L, 102L);
    }

    @Test
    @DisplayName("예약번호가 없는 경우(수동 예약) Booking만 생성한다")
    void createBookings_WithoutReservationNo_ShouldOnlyCreateBookings() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        String startDateTime = now.plusHours(1).toString();
        String endDateTime = now.plusHours(2).toString();

        BookingRequest request = BookingRequest.builder()
                .machineNos(new Long[]{3L})
                .bookingStartAt(startDateTime)
                .bookingEndAt(endDateTime)
                .bookingPeople(2)
                .bookingPlayHole(9)
                .bookingName("김철수")
                .cellNumber("010-9876-5432")
                .bookingChannel(BookingChannel.MANUAL)
                .gameMode(GameMode.STROKE.getValue())
                .gameTime(60)
                .bookingMemo("수동 예약")
                .reserveNo(null)
                .build();

        Booking booking = Booking.builder()
                .bookingNo(103L)
                .machineNo(3L)
                .bookingStartAt(LocalDateTime.parse(startDateTime))
                .bookingEndAt(LocalDateTime.parse(endDateTime))
                .reserveNo(null)
                .build();

        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        // When
        List<Long> result = bookingService.createBookings(request);

        // Then
        verify(reservationRepository, never()).findById(any());
        verify(bookingRepository).save(any(Booking.class));
        verify(notificationService).sendBookingConfirmation(any(Booking.class));
        verify(pushService).sendBookingNotification(any(Booking.class));
        verify(webSocketService).broadcastBookingUpdate(any(Booking.class));
        verify(cacheService).updateBookingCache(anyList());

        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(103L);
    }

    @Test
    @DisplayName("유효하지 않은 예약 상태인 경우 예외를 발생시킨다")
    void createBookings_WithInvalidReservationStatus_ShouldThrowException() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        String startDateTime = now.plusHours(1).toString();
        String endDateTime = now.plusHours(2).toString();

        BookingRequest request = BookingRequest.builder()
                .machineNos(new Long[]{1L})
                .bookingStartAt(startDateTime)
                .bookingEndAt(endDateTime)
                .bookingPeople(2)
                .bookingPlayHole(9)
                .bookingName("박지성")
                .cellNumber("010-5555-6666")
                .bookingChannel(BookingChannel.MOBILE)
                .gameMode(GameMode.STROKE.getValue())
                .gameTime(60)
                .bookingMemo("이미 승인된 예약")
                .reserveNo(2L)
                .build();

        Reservation reservation = mock(Reservation.class);
        when(reservation.getReserveStatus()).thenReturn(ReservationStatus.APPROVAL);
        when(reservationRepository.findById(2L)).thenReturn(Optional.of(reservation));

        // When & Then
        assertThatThrownBy(() -> bookingService.createBookings(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("유효하지 않은 예약 상태입니다");

        verify(reservationRepository).findById(2L);
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    @DisplayName("존재하지 않는 예약번호인 경우 예외를 발생시킨다")
    void createBookings_WithNonExistentReservationNo_ShouldThrowException() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        String startDateTime = now.plusHours(1).toString();
        String endDateTime = now.plusHours(2).toString();

        BookingRequest request = BookingRequest.builder()
                .machineNos(new Long[]{1L})
                .bookingStartAt(startDateTime)
                .bookingEndAt(endDateTime)
                .bookingPeople(2)
                .bookingPlayHole(9)
                .bookingName("이순신")
                .cellNumber("010-7777-8888")
                .bookingChannel(BookingChannel.MANUAL)
                .gameMode(GameMode.STROKE.getValue())
                .gameTime(60)
                .bookingMemo("존재하지 않는 예약")
                .reserveNo(999L)
                .build();

        when(reservationRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> bookingService.createBookings(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("예약 정보를 찾을 수 없습니다");

        verify(reservationRepository).findById(999L);
        verify(bookingRepository, never()).save(any(Booking.class));
    }
}