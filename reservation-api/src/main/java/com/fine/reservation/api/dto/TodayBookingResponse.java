package com.fine.reservation.api.dto;

import com.fine.reservation.domain.enums.BookingChannel;
import com.fine.reservation.domain.enums.GameMode;
import java.time.LocalDateTime;

public record TodayBookingResponse(
        Long bookingNo,
        Long machineNo,
        LocalDateTime bookingStartAt,
        LocalDateTime bookingEndAt,
        Integer bookingPeople,
        Integer bookingPlayHole,
        String bookingName,
        String cellNumber,
        BookingChannel bookingChannel,
        GameMode gameMode,
        Integer gameTime
) {}