package com.fine.reservation.api.service.notification;


import com.fine.reservation.domain.booking.model.Booking;
import org.springframework.stereotype.Service;

@Service
public interface WebSocketService {
    void broadcastBookingUpdate(Booking booking);
    void notifyBookingChange(Long userId, Booking booking);
}