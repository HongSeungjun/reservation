package com.fine.reservation.domain.booking.repository;

import com.fine.reservation.domain.booking.entity.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingJpaRepository extends JpaRepository<BookingEntity, Long> {
    List<BookingEntity> findByBookingStartAtBetween(LocalDateTime start, LocalDateTime end);

    List<BookingEntity> findByReserveNo(Long reserveNo);
}
