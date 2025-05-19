package com.fine.reservation.api.service.notification;


import com.fine.reservation.domain.booking.entity.BookingEntity;
import org.springframework.stereotype.Service;

@Service
public interface NotificationService {
    void sendBookingConfirmation(BookingEntity booking);
    void sendBookingCancellation(BookingEntity booking);
    void sendBookingReminder(BookingEntity booking);
}