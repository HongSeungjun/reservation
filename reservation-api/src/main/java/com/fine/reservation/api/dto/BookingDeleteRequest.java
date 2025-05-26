package com.fine.reservation.api.dto;

import com.fine.reservation.domain.enums.ReservationStatus;
import jakarta.validation.constraints.NotNull;

public record BookingDeleteRequest(
        Long reserveNo,
        @NotNull(message = "bookingNo is require")
        Long bookingNo,
        Integer shopNo,
        ReservationStatus reservationStatus,
        Boolean penalty
) {
}