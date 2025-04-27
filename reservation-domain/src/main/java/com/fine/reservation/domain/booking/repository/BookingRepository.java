package com.fine.reservation.domain.booking.repository;

import com.fine.reservation.domain.booking.entity.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<BookingEntity, Integer> {
     List<BookingEntity> findByReserveNo(Integer reserveNo);
}