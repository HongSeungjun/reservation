package com.fine.reservation.domain.booking.repository;

import com.fine.reservation.domain.booking.entity.BookingEntity;
import com.fine.reservation.domain.booking.mapper.BookingMapper;
import com.fine.reservation.domain.booking.model.Booking;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Repository
public class BookingRepositoryImpl implements BookingRepository {
    private final JpaBookingRepository jpaRepository;
    private final BookingMapper mapper;

    @Override
    public Booking save(Booking booking) {
        BookingEntity entity = mapper.toEntity(booking);
        BookingEntity savedEntity = jpaRepository.save(entity);
        return mapper.toModel(savedEntity);
    }
    
    @Override
    public Optional<Booking> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toModel);
    }

    @Override
    public List<Booking> findAll() {
        List<BookingEntity> entities = jpaRepository.findAll();
        return entities.stream()
                .map(mapper::toModel)
                .toList();
    }

    @Override
    public List<Booking> findByReserveNo(Long reserveNo) {
        List<BookingEntity> bookingEntities = jpaRepository.findByReserveNo(reserveNo);
        return bookingEntities.stream()
                .map(mapper::toModel)
                .toList();
    }


}
