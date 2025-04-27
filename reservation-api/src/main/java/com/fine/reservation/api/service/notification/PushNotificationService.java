package com.fine.reservation.api.service.notification;


import com.fine.reservation.domain.booking.model.Booking;
import org.springframework.stereotype.Service;

@Service
public interface PushNotificationService {
    void sendBookingNotification(Booking booking);
    void sendCancellationNotification(Booking booking);
}