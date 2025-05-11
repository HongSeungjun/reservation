package com.fine.reservation.api.controller;

import com.fine.reservation.api.dto.BookingNoResponse;
import com.fine.reservation.api.dto.BookingRequest;
import com.fine.reservation.api.dto.TodayBookingResponse;
import com.fine.reservation.api.mapper.BookingDtoMapper;
import com.fine.reservation.api.service.BookingService;
import com.fine.reservation.domain.booking.model.Booking;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {

    private final BookingService bookingService;
    private final BookingDtoMapper bookingMapper;

    @PostMapping
    public ResponseEntity<BookingNoResponse> book(@RequestBody @Valid BookingRequest req) {
        List<Long> result = bookingService.createBookings(req);
        return ResponseEntity.ok(new BookingNoResponse(result));
    }

    @GetMapping("/today")
    public ResponseEntity<List<TodayBookingResponse>> getTodayBookings(
            @RequestParam("startAt") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startAt

    ) {
        LocalDate targetDate = startAt != null ? startAt : LocalDate.now();

        List<Booking> todayBookings = bookingService.getBookingsByDate(targetDate);
        return ResponseEntity.ok(bookingMapper.toTodayResponseList(todayBookings));
    }




}