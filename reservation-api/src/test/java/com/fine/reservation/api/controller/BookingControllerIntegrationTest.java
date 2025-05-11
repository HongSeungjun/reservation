package com.fine.reservation.api.controller;

import com.fine.reservation.api.dto.BookingNoResponse;
import com.fine.reservation.api.dto.BookingRequest;
import com.fine.reservation.api.service.notification.NotificationService;
import com.fine.reservation.api.service.notification.PushNotificationService;
import com.fine.reservation.api.service.notification.RedisCacheService;
import com.fine.reservation.api.service.notification.WebSocketService;
import com.fine.reservation.domain.booking.model.Booking;
import com.fine.reservation.domain.booking.repository.BookingRepository;
import com.fine.reservation.domain.enums.BookingChannel;
import com.fine.reservation.domain.enums.ReservationStatus;
import com.fine.reservation.domain.reservation.model.Reservation;
import com.fine.reservation.domain.reservation.repository.ReservationRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class BookingControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private EntityManager entityManager;

    @MockitoBean
    private NotificationService notificationService;

    @MockitoBean
    private PushNotificationService pushNotificationService;

    @MockitoBean
    private WebSocketService webSocketService;

    @MockitoBean
    private RedisCacheService redisCacheService;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Test
    @DisplayName("모바일 예약을 승인 처리하면 예약 상태가 APPROVAL로 변경되고 Booking이 생성된다")
    void testApproveReservationAndCreateBooking() {
        // Given
        Reservation reservation = createAndSaveReservation(ReservationStatus.REQUEST);

        assertThat(reservationRepository.findById(reservation.getReserveNo()).isPresent()).isTrue();
        BookingRequest request = BookingRequest.builder()
                .machineNos(new Long[]{1L, 2L})
                .bookingStartAt(LocalDateTime.now().plusHours(1).format(FORMATTER))
                .bookingEndAt(LocalDateTime.now().plusHours(2).format(FORMATTER))
                .bookingPeople(4)
                .bookingPlayHole(18)
                .bookingName("홍길동")
                .cellNumber("010-1234-5678")
                .bookingChannel(BookingChannel.MOBILE)
                .gameMode(1)
                .gameTime(60)
                .reserveNo(reservation.getReserveNo()) // 예약번호 설정
                .build();

        // When
        ResponseEntity<BookingNoResponse> response = callBookingApi(request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getBookingNo()).hasSize(2);
        
        // 예약 상태가 APPROVAL로 변경되었는지 확인
        Reservation updatedReservation = reservationRepository.findById(reservation.getReserveNo())
                .orElseThrow();
        assertThat(updatedReservation.getReserveStatus()).isEqualTo(ReservationStatus.APPROVAL);
        
        List<Booking> bookings = bookingRepository.findByReserveNo(reservation.getReserveNo());
        assertThat(bookings).hasSize(2);
        assertThat(bookings.get(0).getMachineNo()).isEqualTo(1L);
        assertThat(bookings.get(1).getMachineNo()).isEqualTo(2L);
        
        verify(notificationService, times(2)).sendBookingConfirmation(any(Booking.class));
        verify(pushNotificationService, times(2)).sendBookingNotification(any(Booking.class));
        verify(webSocketService, times(2)).broadcastBookingUpdate(any(Booking.class));
        verify(redisCacheService).updateBookingCache(anyList());
    }

    @Test
    @DisplayName("수동 예약 등록")
    void testManualBookingCreation() {
        // Given
        BookingRequest request = BookingRequest.builder()
                .machineNos(new Long[]{3L})
                .bookingStartAt(LocalDateTime.now().plusHours(2).format(FORMATTER))
                .bookingEndAt(LocalDateTime.now().plusHours(3).format(FORMATTER))
                .bookingPeople(2)
                .bookingPlayHole(9)
                .bookingName("김철수")
                .cellNumber("010-9876-5432")
                .bookingChannel(BookingChannel.MANUAL)
                .gameMode(1)
                .gameTime(60)
                .reserveNo(null)  // 예약번호 없음
                .build();

        // When
        ResponseEntity<BookingNoResponse> response = callBookingApi(request);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getBookingNo()).hasSize(1);
        
        Long bookingNo = response.getBody().getBookingNo().getFirst();
        System.out.println("test bookingNo:"+bookingNo.toString());
        Booking booking = bookingRepository.findById(bookingNo).orElseThrow();
        System.out.println("test booking:"+booking.toString());


        assertThat(booking.getMachineNo()).isEqualTo(3L);
        assertThat(booking.getBookingName()).isEqualTo("김철수");
        assertThat(booking.getBookingChannel()).isEqualTo(BookingChannel.MANUAL);
        assertThat(booking.getReserveNo()).isNull();
        

        verify(notificationService).sendBookingConfirmation(any(Booking.class));
        verify(pushNotificationService).sendBookingNotification(any(Booking.class));
        verify(webSocketService).broadcastBookingUpdate(any(Booking.class));
        verify(redisCacheService).updateBookingCache(anyList());
    }

    @Test
    @DisplayName("예약 인원이 최대 허용치(6명)를 초과하면 Bad Request를 반환한다")
    void testBookingWithExceedingMaxPeople() {
        // Given
        BookingRequest request = BookingRequest.builder()
                .machineNos(new Long[]{1L})
                .bookingStartAt(LocalDateTime.now().plusHours(1).format(FORMATTER))
                .bookingEndAt(LocalDateTime.now().plusHours(2).format(FORMATTER))
                .bookingPeople(7)  // 최대 인원(6명) 초과
                .bookingPlayHole(18)
                .bookingName("이영희")
                .cellNumber("010-5555-5555")
                .bookingChannel(BookingChannel.MOBILE)
                .gameMode(1)
                .build();

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(
                getApiUrl(), createHttpEntity(request), String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        
        verifyNoInteractions(notificationService, pushNotificationService, webSocketService, redisCacheService);
    }

    @Test
    @DisplayName("필수 정보가 누락된 예약 요청은 Bad Request를 반환한다")
    void testBookingWithMissingRequiredFields() {
        // Given
        BookingRequest request = BookingRequest.builder()
                .machineNos(new Long[]{1L})
                // bookingStartAt 누락
                .bookingEndAt(LocalDateTime.now().plusHours(2).format(FORMATTER))
                .bookingPeople(4)
                .bookingPlayHole(18)
                .bookingName("박지성")
                .cellNumber("010-7777-7777")
                .bookingChannel(BookingChannel.MOBILE)
                .gameMode(1)
                .build();

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(
                getApiUrl(), createHttpEntity(request), String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        
        verifyNoInteractions(notificationService, pushNotificationService, webSocketService, redisCacheService);
    }

    @Test
    @DisplayName("존재하지 않는 예약번호로 승인 시도하면 오류를 반환한다")
    void testBookingWithNonExistentReservationNo() {
        // Given
        BookingRequest request = BookingRequest.builder()
                .machineNos(new Long[]{1L})
                .bookingStartAt(LocalDateTime.now().plusHours(1).format(FORMATTER))
                .bookingEndAt(LocalDateTime.now().plusHours(2).format(FORMATTER))
                .bookingPeople(4)
                .bookingPlayHole(18)
                .bookingName("정약용")
                .cellNumber("010-8888-8888")
                .bookingChannel(BookingChannel.MOBILE)
                .gameMode(1)
                .reserveNo(99999L)  // 존재하지 않는 예약번호
                .build();

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(
                getApiUrl(), createHttpEntity(request), String.class);

        // Then
        assertThat(response.getStatusCode().is5xxServerError() || 
                   response.getStatusCode().equals(HttpStatus.NOT_FOUND)).isTrue();
        
        verifyNoInteractions(notificationService, pushNotificationService, webSocketService, redisCacheService);
    }

    @Test
    @DisplayName("이미 승인된 예약을 다시 승인하려고 하면 오류를 반환한다")
    void testBookingWithAlreadyApprovedReservation() {
        // Given
        Reservation alreadyApprovedReservation = createAndSaveReservation(ReservationStatus.APPROVAL);

        BookingRequest request = BookingRequest.builder()
                .machineNos(new Long[]{1L})
                .bookingStartAt(LocalDateTime.now().plusHours(1).format(FORMATTER))
                .bookingEndAt(LocalDateTime.now().plusHours(2).format(FORMATTER))
                .bookingPeople(4)
                .bookingPlayHole(18)
                .bookingName("신사임당")
                .cellNumber("010-9999-9999")
                .bookingChannel(BookingChannel.MOBILE)
                .gameMode(1)
                .reserveNo(alreadyApprovedReservation.getReserveNo())
                .build();

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(
                getApiUrl(), createHttpEntity(request), String.class);

        // Then
        assertThat(response.getStatusCode().is4xxClientError() || 
                   response.getStatusCode().is5xxServerError()).isTrue();
        
        verifyNoInteractions(notificationService, pushNotificationService, webSocketService, redisCacheService);
    }

    private Reservation createAndSaveReservation(ReservationStatus status) {
        Reservation entity = Reservation.builder()
                .reserveStatus(status)
                .reserveName("테스트 예약")
                .reservePhoneNumber("010-1234-5678")
                .reservePeople(4)
                .reserveStartAt(LocalDateTime.now().plusHours(1))
                .reserveEndAt(LocalDateTime.now().plusHours(2))
                .build();

        Reservation savedReservation = reservationRepository.save(entity);

        return savedReservation;
    }

    private ResponseEntity<BookingNoResponse> callBookingApi(BookingRequest request) {
        return restTemplate.postForEntity(
                getApiUrl(), createHttpEntity(request), BookingNoResponse.class);
    }

    private HttpEntity<BookingRequest> createHttpEntity(BookingRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        return new HttpEntity<>(request, headers);
    }

    private String getApiUrl() {
        return "http://localhost:" + port + "/api/bookings";
    }

}