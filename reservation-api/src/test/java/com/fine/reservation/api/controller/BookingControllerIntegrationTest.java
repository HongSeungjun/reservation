package com.fine.reservation.api.controller;

import com.fine.reservation.api.dto.BookingRequest;
import com.fine.reservation.api.dto.BookingResponse;
import com.fine.reservation.api.dto.BookingUpdateTimeRequest;
import com.fine.reservation.api.service.notification.NotificationService;
import com.fine.reservation.api.service.notification.PushNotificationService;
import com.fine.reservation.api.service.notification.RedisCacheService;
import com.fine.reservation.api.service.notification.WebSocketService;
import com.fine.reservation.domain.booking.entity.BookingEntity;
import com.fine.reservation.domain.booking.repository.BookingRepository;
import com.fine.reservation.domain.enums.BookingChannel;
import com.fine.reservation.domain.enums.GameMode;
import com.fine.reservation.domain.enums.ReservationStatus;
import com.fine.reservation.domain.reservation.entity.ReservationEntity;
import com.fine.reservation.domain.reservation.repository.ReservationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
    private BookingRepository bookingRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Test
    @DisplayName("모바일 예약 승인 시 APPROVAL로 변경되고 Booking 생성")
    void testApproveReservationAndCreateBooking() {
        // Given
        BookingRequest request = new BookingRequest(
                List.of(1, 2),
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
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
        HttpEntity<BookingRequest> entity = new HttpEntity<>(request, headers);

        // when
        ResponseEntity<List<Long>> response = restTemplate.exchange(
                "http://localhost:" + port + "/bookings",
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<List<Long>>() {
                }
        );

        // Then
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).hasSize(2);

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
        BookingRequest request = new BookingRequest(
                List.of(3),
                LocalDateTime.now().plusHours(2),
                LocalDateTime.now().plusHours(3),
                2,
                9,
                "김철수",
                "010-9876-5432",
                null,
                BookingChannel.MANUAL,
                GameMode.STROKE,
                60,
                null
                , 1000
        );

        // When
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<BookingRequest> entity = new HttpEntity<>(request, headers);

        // when
        ResponseEntity<List<Long>> response = restTemplate.exchange(
                "http://localhost:" + port + "/bookings",
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<List<Long>>() {
                }
        );
        // Then
        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).hasSize(1);
        Long bookingNo = response.getBody().get(0);

        BookingEntity b = bookingRepository.findById(bookingNo).orElseThrow();
        assertThat(b.getMachineNo()).isEqualTo(3);
        assertThat(b.getReserveNo()).isNull();
    }

    @Test
    @DisplayName("최대 인원 초과 시 Bad Request")
    void testBookingWithExceedingMaxPeople() {
        BookingRequest request = new BookingRequest(
                List.of(1),
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
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
                "http://localhost:" + port + "/bookings", new HttpEntity<>(request, headers), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("필수 정보 누락 시 Bad Request")
    void testBookingWithMissingRequiredFields() {
        BookingRequest request = new BookingRequest(
                List.of(1),
                null,
                LocalDateTime.now().plusHours(2),
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
                "http://localhost:" + port + "/bookings", new HttpEntity<>(request, headers), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("존재하지 않는 예약번호로 승인 시 오류")
    void testBookingWithNonExistentReservationNo() {
        BookingRequest request = new BookingRequest(
                List.of(1),
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2),
                4,
                18,
                "정약용",
                "010-8888-8888",
                null,
                BookingChannel.MOBILE,
                GameMode.STROKE,
                60,
                999L
                , 1000
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<String> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/bookings", new HttpEntity<>(request, headers), String.class);

        assertThat(response.getStatusCode().is5xxServerError() || response.getStatusCode().equals(HttpStatus.NOT_FOUND)).isTrue();
    }

    @Test
    @DisplayName("이미 승인된 예약 재승인 시 오류")
    void testBookingWithAlreadyApprovedReservation() {
        // Given
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = LocalDateTime.now().plusHours(2);
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
                "http://localhost:" + port + "/bookings", new HttpEntity<>(request, headers), String.class);

        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
    }

    @Test
    @DisplayName("특정 날짜 기준 예약 목록 조회")
    void getBookingsBySpecificDateTest() {
        LocalDate startAt = LocalDate.now().plusDays(1);
        LocalDate endAt = LocalDate.now().plusDays(2);
        String startAtStr = startAt.toString();
        String endAtAtStr = endAt.toString();

        ResponseEntity<List<BookingResponse>> response = restTemplate.exchange(
                "http://localhost:" + port + "/bookings?startAt=" + startAtStr + "&endAt=" + endAtAtStr,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<BookingResponse>>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSizeGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("단독 예약 업데이트 (요청된 machineNo 1개) - 대상 예약만 수정")
    void testUpdate_StandaloneBooking_WithSingleMachineInRequest_UpdatesOnlyTarget() {
        Long bookingNoToUpdate = 301L; // reserve_no가 없는 단독 예약
        Integer shopNo = 101;

        BookingRequest request = new BookingRequest(
                List.of(3),
                LocalDateTime.parse("2025-06-10T10:30:00"),
                LocalDateTime.parse("2025-06-10T11:30:00"),
                3, 18, "김단독수정", "010-3333-4444", null,
                BookingChannel.MANUAL, GameMode.STROKE, 60,
                null,
                shopNo
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<BookingRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<List<Long>> response = restTemplate.exchange(
                "/bookings/" + bookingNoToUpdate,
                HttpMethod.PUT,
                entity,
                new ParameterizedTypeReference<List<Long>>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull().hasSize(1).containsExactly(bookingNoToUpdate);

        BookingEntity updatedBooking = bookingRepository.findById(bookingNoToUpdate).orElseThrow();
        assertThat(updatedBooking.getBookingStartAt()).isEqualTo(request.bookingStartAt());
        assertThat(updatedBooking.getBookerName()).isEqualTo("김단독수정");
        assertThat(updatedBooking.getReserveNo()).isNull();
    }

    @Test
    @DisplayName("단독 예약 업데이트 (요청된 machineNo 여러개, 요청에 reserveNo 없음) - 기존 단독 예약 삭제 후 여러개 신규 생성")
    void testUpdate_StandaloneBooking_WithMultipleMachinesAndNoReserveNoInRequest_ReplacesWithNewBookings() {
        Long originalBookingNo = 301L; // reserve_no가 없는 단독 예약
        Integer shopNo = 101;

        BookingRequest request = new BookingRequest(
                List.of(5, 6),
                LocalDateTime.parse("2025-06-11T10:00:00"),
                LocalDateTime.parse("2025-06-11T11:00:00"),
                4, 18, "박다중", "010-4444-5555", "여러 기기로 변경",
                BookingChannel.MANUAL, GameMode.CHIP_AND_PUTT, 60,
                null,
                shopNo
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<BookingRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<List<Long>> response = restTemplate.exchange(
                "/bookings/" + originalBookingNo,
                HttpMethod.PUT,
                entity,
                new ParameterizedTypeReference<List<Long>>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull().hasSize(2);
        assertThat(bookingRepository.findById(originalBookingNo)).isEmpty();

        response.getBody().forEach(newBookingNo -> {
            BookingEntity newBooking = bookingRepository.findById(newBookingNo).orElseThrow();
            assertThat(newBooking.getShopNo()).isEqualTo(request.shopNo());
            assertThat(request.machineNos()).contains(newBooking.getMachineNo());
            assertThat(newBooking.getBookerName()).isEqualTo("박다중");
            assertThat(newBooking.getReserveNo()).isNull();
        });
    }

    @Test
    @DisplayName("다중 방 예약 중 한 방 업데이트 (요청된 machineNo 1개) - 해당 방만 수정되고 다른 방은 유지")
    void testUpdate_SingleRoomOfMultiRoomReservation_WithSingleMachineInRequest_UpdatesOnlyTargetedRoom() {
        Long bookingNoToUpdate = 201L; // reserve_no=2 에 연결된 방 중 하나
        Long unaffectedBookingNoInSameReservation = 202L; // reserve_no=2 에 연결된 다른 방
        Long reserveNo = 2L;
        Integer shopNo = 101;

        BookingRequest request = new BookingRequest(
                List.of(1), // 업데이트 대상 방의 machineNo
                LocalDateTime.parse("2025-06-12T12:30:00"), // 시간 변경
                LocalDateTime.parse("2025-06-12T13:30:00"),
                3, 18, "이일부수정", "010-2222-3333", "201번 방만 시간 변경",
                BookingChannel.MOBILE, GameMode.STROKE, 60,
                reserveNo, // 원래 예약 번호 전달
                shopNo
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<BookingRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<List<Long>> response = restTemplate.exchange(
                "/bookings/" + bookingNoToUpdate,
                HttpMethod.PUT,
                entity,
                new ParameterizedTypeReference<List<Long>>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull().hasSize(1).containsExactly(bookingNoToUpdate);

        BookingEntity updatedBooking = bookingRepository.findById(bookingNoToUpdate).orElseThrow();
        assertThat(updatedBooking.getBookingStartAt()).isEqualTo(request.bookingStartAt());
        assertThat(updatedBooking.getBookerName()).isEqualTo("이일부수정");
        assertThat(updatedBooking.getReserveNo()).isEqualTo(reserveNo);

        BookingEntity unaffectedBooking = bookingRepository.findById(unaffectedBookingNoInSameReservation).orElseThrow();
        assertThat(unaffectedBooking.getBookerName()).isEqualTo("이영희"); // test-data.sql의 원본 값
        assertThat(unaffectedBooking.getBookingStartAt()).isNotEqualTo(request.bookingStartAt()); // 시간 변경 안됨
    }

    @Test
    @DisplayName("다중 방 예약 업데이트 (요청된 machineNo 여러개, 요청에 reserveNo 있음) - 해당 reserveNo의 모든 방 교체")
    void testUpdate_MultiRoomReservation_WithMultipleMachinesAndReserveNoInRequest_ReplacesAllAssociatedRooms() {
        Long bookingNoForIdentifyingReservation = 201L; // reserve_no=2 식별용
        Long reserveNo = 2L;
        Integer shopNo = 101;

        BookingRequest request = new BookingRequest(
                List.of(7, 8), // 새로운 방 목록
                LocalDateTime.parse("2025-06-13T14:00:00"),
                LocalDateTime.parse("2025-06-13T15:00:00"),
                2, 9, "최전체교체", "010-5555-7777", "reserve_no 2번 전체 방 교체",
                BookingChannel.KIOSK, GameMode.STROKE, 60,
                reserveNo, // 이 reserveNo에 해당하는 모든 방이 교체 대상
                shopNo
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<BookingRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<List<Long>> response = restTemplate.exchange(
                "/bookings/" + bookingNoForIdentifyingReservation,
                HttpMethod.PUT,
                entity,
                new ParameterizedTypeReference<List<Long>>() {
                }
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull().hasSize(2);

        assertThat(bookingRepository.findById(201L)).isEmpty(); // 기존 방(201) 삭제 확인
        assertThat(bookingRepository.findById(202L)).isEmpty(); // 기존 방(202) 삭제 확인

        response.getBody().forEach(newBookingNo -> {
            BookingEntity newBooking = bookingRepository.findById(newBookingNo).orElseThrow();
            assertThat(newBooking.getShopNo()).isEqualTo(request.shopNo());
            assertThat(newBooking.getReserveNo()).isEqualTo(reserveNo);
            assertThat(request.machineNos()).contains(newBooking.getMachineNo());
            assertThat(newBooking.getBookerName()).isEqualTo("최전체교체");
        });
    }

    @Test
    @DisplayName("예약 업데이트 실패 - 존재하지 않는 예약번호 (404 Not Found)")
    void testUpdateBooking_Fail_NotFound() {
        Long nonExistentBookingNo = 9999L;
        Integer shopNo = 101;
        BookingRequest request = new BookingRequest(
                List.of(1), LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(1),
                1, 18, "Test", "010-0000-0000", null, BookingChannel.MANUAL, GameMode.STROKE, 60,
                null, shopNo
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<BookingRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "/bookings/" + nonExistentBookingNo,
                HttpMethod.PUT,
                entity,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("예약 업데이트 실패 - 시간 중복 (409 Conflict)")
    void testUpdateBooking_Fail_Conflict() {
        Long bookingNoToUpdate = 301L; // 단독 예약(301)을
        Integer shopNo = 101;

        // booking_schedule 401번의 시간/기기로 변경 시도 (test-data.sql에서 401번은 충돌 유발용)
        Integer conflictingMachineNo = 4;
        LocalDateTime conflictingStartTime = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(14, 0, 0));
        LocalDateTime conflictingEndTime = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(15, 0, 0));

        BookingRequest request = new BookingRequest(
                List.of(conflictingMachineNo), conflictingStartTime, conflictingEndTime,
                2, 18, "김충돌시도", "010-1212-3434", null,
                BookingChannel.MANUAL, GameMode.STROKE, 60,
                null, shopNo
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<BookingRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "/bookings/" + bookingNoToUpdate,
                HttpMethod.PUT,
                entity,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    @DisplayName("PATCH /bookings/{bookingNo} - 예약 시간 및 방 변경 성공 (202 Accepted)")
    void testUpdateBookingTime_AndTimeAndMachine_Success() {
        Long bookingNoToUpdate = 301L;
        Integer shopNo = 101;
        Integer originalMachineNo = 3;

        BookingEntity originalBooking = bookingRepository.findById(bookingNoToUpdate).orElseThrow();

        BookingUpdateTimeRequest request = new BookingUpdateTimeRequest(
                originalMachineNo + 10,
                LocalDateTime.parse("2025-07-01T10:00:00"),
                LocalDateTime.parse("2025-07-01T11:00:00"),
                shopNo
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<BookingUpdateTimeRequest> entity = new HttpEntity<>(request, headers);

        // When
        ResponseEntity<Void> response = restTemplate.exchange(
                "/bookings/" + bookingNoToUpdate,
                HttpMethod.PATCH,
                entity,
                Void.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED); // 202

        BookingEntity updatedBooking = bookingRepository.findById(bookingNoToUpdate).orElseThrow();
        assertThat(updatedBooking.getMachineNo()).isEqualTo(request.machineNo());
        assertThat(updatedBooking.getBookingStartAt()).isEqualTo(request.bookingStartAt());
        assertThat(updatedBooking.getBookingEndAt()).isEqualTo(request.bookingEndAt());
        assertThat(updatedBooking.getUpdatedAt()).isAfter(originalBooking.getUpdatedAt()); // updatedAt 갱신 확인
        assertThat(updatedBooking.getGameDurationMinutes()).isEqualTo(originalBooking.getGameDurationMinutes());
        assertThat(updatedBooking.getBookerName()).isEqualTo(originalBooking.getBookerName());

        // WebSocketService.broadcastBookingUpdate 호출 검증
        ArgumentCaptor<BookingEntity> bookingEntityCaptor = ArgumentCaptor.forClass(BookingEntity.class);
        verify(webSocketService, times(1)).broadcastBookingUpdate(bookingEntityCaptor.capture());
        BookingEntity broadcastedBooking = bookingEntityCaptor.getValue();
        assertThat(broadcastedBooking.getBookingNo()).isEqualTo(bookingNoToUpdate);
        assertThat(broadcastedBooking.getMachineNo()).isEqualTo(request.machineNo());
        assertThat(broadcastedBooking.getBookingStartAt()).isEqualTo(request.bookingStartAt());
    }

    @Test
    @DisplayName("PATCH /bookings/{bookingNo} - 예약 시간만 변경 성공 (방 번호 동일, 202 Accepted)")
    void testUpdateBookingTime_OnlyTime_Success() {
        Long bookingNoToUpdate = 301L;
        BookingEntity originalBooking = bookingRepository.findById(bookingNoToUpdate).orElseThrow();
        Integer originalMachineNo = originalBooking.getMachineNo();

        BookingUpdateTimeRequest request = new BookingUpdateTimeRequest(
                originalMachineNo,
                LocalDateTime.parse("2025-07-02T11:00:00"),
                LocalDateTime.parse("2025-07-02T12:00:00"),
                101
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<BookingUpdateTimeRequest> entity = new HttpEntity<>(request, headers);

        // When
        ResponseEntity<Void> response = restTemplate.exchange(
                "/bookings/" + bookingNoToUpdate,
                HttpMethod.PATCH,
                entity,
                Void.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);

        BookingEntity updatedBooking = bookingRepository.findById(bookingNoToUpdate).orElseThrow();
        assertThat(updatedBooking.getMachineNo()).isEqualTo(originalMachineNo); // 방 번호 동일
        assertThat(updatedBooking.getBookingStartAt()).isEqualTo(request.bookingStartAt());
        assertThat(updatedBooking.getBookingEndAt()).isEqualTo(request.bookingEndAt());
        assertThat(updatedBooking.getUpdatedAt()).isAfter(originalBooking.getUpdatedAt());

        verify(webSocketService, times(1)).broadcastBookingUpdate(any(BookingEntity.class));
    }


    @Test
    @DisplayName("PATCH /bookings/{bookingNo} - 존재하지 않는 예약번호 (404 Not Found)")
    void testUpdateBookingTime_Fail_NotFound() {
        Long nonExistentBookingNo = 9999L;
        BookingUpdateTimeRequest request = new BookingUpdateTimeRequest(
                1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1).plusHours(1), 101
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<BookingUpdateTimeRequest> entity = new HttpEntity<>(request, headers);

        // When
        ResponseEntity<String> response = restTemplate.exchange(
                "/bookings/" + nonExistentBookingNo,
                HttpMethod.PATCH,
                entity,
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(webSocketService, never()).broadcastBookingUpdate(any(BookingEntity.class));
    }

    @Test
    @DisplayName("PATCH /bookings/{bookingNo} - 시간 중복 (409 Conflict)")
    void testUpdateBookingTime_Fail_Conflict() {
        Long bookingNoToUpdate = 301L;
        Integer conflictingMachineNo = 4;
        LocalDateTime conflictingStartTime = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(14, 0, 0)); // 401번의 시간
        LocalDateTime conflictingEndTime = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(15, 0, 0));   // 401번의 시간

        BookingUpdateTimeRequest request = new BookingUpdateTimeRequest(
                conflictingMachineNo,
                conflictingStartTime,
                conflictingEndTime,
                101
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<BookingUpdateTimeRequest> entity = new HttpEntity<>(request, headers);

        // When
        ResponseEntity<String> response = restTemplate.exchange(
                "/bookings/" + bookingNoToUpdate,
                HttpMethod.PATCH,
                entity,
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        verify(webSocketService, never()).broadcastBookingUpdate(any(BookingEntity.class));
    }

    @Test
    @DisplayName("PATCH /bookings/{bookingNo} - 잘못된 요청 데이터 (예: 시작시간 누락, 400 Bad Request)")
    void testUpdateBookingTime_Fail_InvalidRequest() {
        Long bookingNoToUpdate = 301L;
        BookingUpdateTimeRequest invalidRequest = new BookingUpdateTimeRequest(
                1, null, LocalDateTime.now().plusHours(1),101
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<BookingUpdateTimeRequest> entity = new HttpEntity<>(invalidRequest, headers);

        // When
        ResponseEntity<String> response = restTemplate.exchange(
                "/bookings/" + bookingNoToUpdate,
                HttpMethod.PATCH,
                entity,
                String.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verify(webSocketService, never()).broadcastBookingUpdate(any(BookingEntity.class));
    }
}
