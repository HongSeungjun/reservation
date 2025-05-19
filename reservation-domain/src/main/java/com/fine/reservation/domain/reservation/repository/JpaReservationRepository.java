package com.fine.reservation.domain.reservation.repository;

import com.fine.reservation.domain.reservation.entity.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaReservationRepository extends JpaRepository<ReservationEntity, Long> {
}
