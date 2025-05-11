package com.fine.reservation.api.controller;

import com.fine.reservation.api.dto.BookingNoResponse;
import com.fine.reservation.api.dto.BookingRequest;
import com.fine.reservation.api.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingNoResponse> book(@RequestBody @Valid BookingRequest req) {
        List<Long> result = bookingService.createBookings(req);
        return ResponseEntity.ok(new BookingNoResponse(result));
    }
}