package com.fine.reservation.domain.booking.repository;

import com.fine.reservation.domain.booking.entity.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<BookingEntity, Long> {
    List<BookingEntity> findByBookingStartAtBetween(LocalDateTime start, LocalDateTime end);

    List<BookingEntity> findByReserveNo(Long reserveNo);

    Integer deleteByBookingNoAndShopNo(Long bookingNo, Integer shopNo);
    Integer deleteByReserveNoAndShopNo(Long reserveNo, Integer shopNo);

    @Query("""
      SELECT CASE WHEN COUNT(b) > 0 THEN TRUE ELSE FALSE END
      FROM BookingEntity b
      WHERE b.machineNo       = :machineNo
        AND b.bookingStartAt <  :end
        AND b.bookingEndAt   >  :start
        AND b.bookingNo      <> :excludeBookingNo
    """)
    boolean existsOverlap(
            @Param("machineNo") Integer machineNo,
            @Param("start") LocalDateTime newStart,
            @Param("end") LocalDateTime newEnd,
            @Param("excludeBookingNo") Long bookingNo
    );
}
