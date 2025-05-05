package com.fine.reservation.api.service.notification;


import com.fine.reservation.domain.booking.model.Booking;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RedisCacheService {
    void updateBookingCache(List<Booking> bookings);
    void invalidateUserBookings(Long userId);
    List<Booking> getUserActiveBookings(Long userId);
}