package com.fine.reservation.domain.booking.repository;

import com.fine.reservation.domain.booking.model.Booking;

import java.util.List;
import java.util.Optional;

public interface BookingRepository{
     Booking save(Booking booking);
     Optional<Booking> findById(Long id);
     List<Booking> findAll();

     List<Booking> findByReserveNo(Long reserveNo);
}