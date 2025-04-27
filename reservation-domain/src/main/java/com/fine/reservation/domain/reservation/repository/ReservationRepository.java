package com.fine.reservation.domain.reservation.repository;

import com.fine.reservation.domain.reservation.model.Reservation;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository {
     Reservation save(Reservation reservation);
     Optional<Reservation> findById(Long id);
     List<Reservation> findAll();
}