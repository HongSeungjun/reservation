package com.fine.reservation.api.service.notification;


import com.fine.reservation.domain.booking.entity.BookingEntity;
import org.springframework.stereotype.Service;

@Service
public interface PushNotificationService {
    void sendBookingNotification(BookingEntity booking);
    void sendCancellationNotification(BookingEntity booking);
}