package com.fine.reservation.domain.reservation.repository;

import com.fine.reservation.domain.reservation.entity.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<ReservationEntity, Integer> {
     List<ReservationEntity> findByShopNo(Integer shopNo);
}
