package com.fine.reservation.api.service.notification;


import com.fine.reservation.domain.booking.model.Booking;
import org.springframework.stereotype.Service;

@Service
public interface NotificationService {
    void sendBookingConfirmation(Booking booking);
    void sendBookingCancellation(Booking booking);
    void sendBookingReminder(Booking booking);
}