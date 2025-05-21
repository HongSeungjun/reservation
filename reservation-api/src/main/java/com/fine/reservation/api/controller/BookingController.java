package com.fine.reservation.api.controller;

import com.fine.reservation.api.dto.BookingNoResponse;
import com.fine.reservation.api.dto.BookingRequest;
import com.fine.reservation.api.dto.BookingResponse;
import com.fine.reservation.api.mapper.BookingDtoMapper;
import com.fine.reservation.api.service.BookingService;
import com.fine.reservation.domain.booking.entity.BookingEntity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final BookingDtoMapper bookingMapper;

    @PostMapping
    public ResponseEntity<BookingNoResponse> book(@RequestBody @Valid BookingRequest req) {
        List<Long> result = bookingService.createBookings(req);
        return ResponseEntity.ok(new BookingNoResponse(result));
    }

    @GetMapping
    public ResponseEntity<List<BookingResponse>> getBookings(
            @RequestParam("startAt") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startAt,
            @RequestParam("endAt") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endAt

    ) {
        if (startAt.isAfter(endAt)) {
            throw new IllegalArgumentException("종료 날짜(endAt)는 시작 날짜(startAt)보다 이전일 수 없습니다.");
        }

        List<BookingEntity> bookings = bookingService.getBookingsByDateRange(startAt, endAt);
        return ResponseEntity.ok(bookingMapper.toResponseList(bookings));
    }


}