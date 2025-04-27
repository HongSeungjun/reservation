package com.fine.reservation.domain.booking.repository;

import com.fine.reservation.domain.booking.entity.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface JpaBookingRepository extends JpaRepository<BookingEntity, Long> {
    List<BookingEntity> findByReserveNo(Long reserveNo);
}
