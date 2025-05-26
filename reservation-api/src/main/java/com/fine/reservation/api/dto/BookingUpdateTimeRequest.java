package com.fine.reservation.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fine.reservation.domain.enums.BookingChannel;
import com.fine.reservation.domain.enums.GameMode;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDateTime;
import java.util.List;

public record BookingUpdateTimeRequest(
        @NotNull(message = "invalid parameter - machineNo")
        Integer machineNo,

        @NotNull(message = "invalid parameter - bookingStartAt")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime bookingStartAt,

        @NotNull(message = "invalid parameter - bookingEndAt")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime bookingEndAt,

        Integer shopNo
) {}