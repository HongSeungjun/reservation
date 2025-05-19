package com.fine.reservation.api.controller;

import com.fine.reservation.api.dto.BookingNoResponse;
import com.fine.reservation.api.dto.BookingRequest;
import com.fine.reservation.api.dto.TodayBookingResponse;
import com.fine.reservation.api.service.notification.NotificationService;
import com.fine.reservation.api.service.notification.PushNotificationService;
import com.fine.reservation.api.service.notification.RedisCacheService;
import com.fine.reservation.api.service.notification.WebSocketService;
import com.fine.reservation.domain.booking.entity.BookingEntity;
import com.fine.reservation.domain.booking.repository.BookingJpaRepository;
import com.fine.reservation.domain.enums.BookingChannel;
import com.fine.reservation.domain.enums.GameMode;
import com.fine.reservation.domain.enums.ReservationStatus;
import com.fine.reservation.domain.reservation.entity.ReservationEntity;
import com.fine.reservation.domain.reservation.repository.ReservationJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class BookingControllerIntegrationTest {

    @MockitoBean
    private NotificationService notificationService;

    @MockitoBean
    private PushNotificationService pushNotificationService;

    @MockitoBean
    private WebSocketService webSocketService;

    @MockitoBean
    private RedisCacheService redisCacheService;
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private BookingJpaRepository bookingRepository;

    @Autowired
    private ReservationJpaRepository reservationRepository;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Test
    @DisplayName("모바일 예약 승인 시 APPROVAL로 변경되고 Booking 생성")
    void testApproveReservationAndCreateBooking() {
        // Given:

        String start = LocalDateTime.now().plusHours(1).format(FORMATTER);
        String end   = LocalDateTime.now().plusHours(2).format(FORMATTER);
        BookingRequest request = new BookingRequest(
                List.of(1, 2),
                start,
                end,
                4,
                18,
                "홍길동",
                "010-1234-5678",
                null,
                BookingChannel.MOBILE,
                GameMode.STROKE,
                60,
                1L,
                1000
        );

        // When
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<BookingNoResponse> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/bookings", new HttpEntity<>(request, headers), BookingNoResponse.class);

        // Then
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody().getBookingNo()).hasSize(2);

        // 예약 상태 변경 확인
        ReservationEntity updated = reservationRepository.findById(1L).orElseThrow();
        assertThat(updated.getReserveStatus()).isEqualTo(ReservationStatus.APPROVAL);

        List<BookingEntity> bookings = bookingRepository.findByReserveNo(1L);
        assertThat(bookings).hasSize(2)
                .extracting(BookingEntity::getMachineNo)
                .containsExactlyInAnyOrder(1, 2);
    }

    @Test
    @DisplayName("수동 예약 생성")
    void testManualBookingCreation() {
        String start = LocalDateTime.now().plusHours(2).format(FORMATTER);
        String end   = LocalDateTime.now().plusHours(3).format(FORMATTER);
        BookingRequest request = new BookingRequest(
                List.of(3),
                start,
                end,
                2,
                9,
                "김철수",
                "010-9876-5432",
                null,
                BookingChannel.MANUAL,
                GameMode.STROKE,
                60,
                null
                ,1000
        );

        // When
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<BookingNoResponse> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/bookings", new HttpEntity<>(request, headers), BookingNoResponse.class);

        // Then
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody().getBookingNo()).hasSize(1);
        Long bookingNo = response.getBody().getBookingNo().get(0);

        BookingEntity b = bookingRepository.findById(bookingNo).orElseThrow();
        assertThat(b.getMachineNo()).isEqualTo(3);
        assertThat(b.getReserveNo()).isNull();
    }

    @Test
    @DisplayName("최대 인원 초과 시 Bad Request")
    void testBookingWithExceedingMaxPeople() {
        BookingRequest request = new BookingRequest(
                List.of(1),
                LocalDateTime.now().plusHours(1).format(FORMATTER),
                LocalDateTime.now().plusHours(2).format(FORMATTER),
                7,
                18,
                "이영희",
                "010-5555-5555",
                null,
                BookingChannel.MOBILE,
                GameMode.STROKE,
                60,
                null,
                1000
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/bookings", new HttpEntity<>(request, headers), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("필수 정보 누락 시 Bad Request")
    void testBookingWithMissingRequiredFields() {
        BookingRequest request = new BookingRequest(
                List.of(1),
                null,
                LocalDateTime.now().plusHours(2).format(FORMATTER),
                4,
                18,
                "박지성",
                "010-7777-7777",
                null,
                BookingChannel.MOBILE,
                GameMode.STROKE,
                60,
                null,
                1000
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/bookings", new HttpEntity<>(request, headers), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("존재하지 않는 예약번호로 승인 시 오류")
    void testBookingWithNonExistentReservationNo() {
        BookingRequest request = new BookingRequest(
                List.of(1),
                LocalDateTime.now().plusHours(1).format(FORMATTER),
                LocalDateTime.now().plusHours(2).format(FORMATTER),
                4,
                18,
                "정약용",
                "010-8888-8888",
                null,
                BookingChannel.MOBILE,
                GameMode.STROKE,
                60,
                999L
                ,1000
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/bookings", new HttpEntity<>(request, headers), String.class);

        assertThat(response.getStatusCode().is5xxServerError() || response.getStatusCode().equals(HttpStatus.NOT_FOUND)).isTrue();
    }

    @Test
    @DisplayName("이미 승인된 예약 재승인 시 오류")
    void testBookingWithAlreadyApprovedReservation() {
        // Given
        String start = LocalDateTime.now().plusHours(1).format(FORMATTER);
        String end   = LocalDateTime.now().plusHours(2).format(FORMATTER);
        BookingRequest request = new BookingRequest(
                List.of(1),
                start,
                end,
                4,
                18,
                "이영희",
                "010-5555-6666",
                null,
                BookingChannel.MOBILE,
                GameMode.STROKE,
                60,
                2L,
                1000
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/bookings", new HttpEntity<>(request, headers), String.class);

        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
    }

    @Test
    @DisplayName("특정 날짜 기준 예약 목록 조회")
    void getBookingsBySpecificDateTest() {
        LocalDate target = LocalDate.now().plusDays(1);
        String dateStr = target.toString();

        ResponseEntity<List<TodayBookingResponse>> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/bookings/today?startAt=" + dateStr,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<TodayBookingResponse>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSizeGreaterThanOrEqualTo(0);
    }
}
