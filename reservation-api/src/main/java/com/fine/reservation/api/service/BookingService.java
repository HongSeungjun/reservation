package com.fine.reservation.api.service;

import com.fine.reservation.api.dto.BookingRequest;
import com.fine.reservation.domain.booking.model.Booking;

import java.util.List;

public interface BookingService {
    List<Long> createBookings(BookingRequest request);
}