package com.fine.reservation.api.service.notification;


import com.fine.reservation.domain.booking.entity.BookingEntity;
import org.springframework.stereotype.Service;

@Service
public interface WebSocketService {
    void broadcastBookingUpdate(BookingEntity booking);
    void notifyBookingChange(Long userId, BookingEntity booking);
}