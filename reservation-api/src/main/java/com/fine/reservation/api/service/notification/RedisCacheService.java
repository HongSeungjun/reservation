package com.fine.reservation.api.service.notification;


import com.fine.reservation.domain.booking.entity.BookingEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RedisCacheService {
    void updateBookingCache(List<BookingEntity> bookings);
    void invalidateUserBookings(Long userId);
    List<BookingEntity> getUserActiveBookings(Long userId);
}