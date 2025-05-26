package com.fine.reservation.domain.reservation.repository;

import com.fine.reservation.domain.reservation.entity.PenaltyCountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PenaltyCountRepository extends JpaRepository<PenaltyCountEntity, Long> {

    Optional<PenaltyCountEntity> findByUserNo(Integer userNo);
}
