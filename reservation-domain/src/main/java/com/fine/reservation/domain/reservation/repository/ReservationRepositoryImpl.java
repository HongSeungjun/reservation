package com.fine.reservation.domain.reservation.repository;

import com.fine.reservation.domain.reservation.entity.ReservationEntity;
import com.fine.reservation.domain.reservation.mapper.ReservationMapper;
import com.fine.reservation.domain.reservation.model.Reservation;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Repository
public class ReservationRepositoryImpl implements ReservationRepository {
    private final JpaReservationRepository jpaRepository;
    private final ReservationMapper mapper;

    @Override
    public Reservation save(Reservation reservation) {
        ReservationEntity entity = mapper.toEntity(reservation);
        ReservationEntity savedEntity = jpaRepository.saveAndFlush(entity);
        return mapper.toModel(savedEntity);
    }
    
    @Override
    public Optional<Reservation> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toModel);
    }

    @Override
    public List<Reservation> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toModel)
                .toList();
    }
}
