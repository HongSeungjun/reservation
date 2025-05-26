package com.fine.reservation.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fine.reservation.domain.enums.BookingChannel;
import com.fine.reservation.domain.enums.GameMode;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDateTime;
import java.util.List;

public record BookingRequest(
        @NotNull(message = "invalid parameter - machineNos")
        List<Integer> machineNos,

        @NotNull(message = "invalid parameter - bookingStartAt")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime bookingStartAt,

        @NotNull(message = "invalid parameter - bookingEndAt")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime bookingEndAt,

        @NotNull(message = "invalid parameter - peopleCount")
        @Range(min = 1, max = 6, message = "peopleCount min 1 max 6")
        Integer peopleCount,

        @NotNull(message = "invalid parameter - holeCount")
        Integer holeCount,

        @NotNull(message = "invalid parameter - bookerName")
        String  bookerName,

        @NotNull(message = "invalid parameter - phoneNumber")
        String  phoneNumber,

        String  bookingMemo,

        @NotNull(message = "invalid parameter - bookingChannel")
        BookingChannel bookingChannel,

        @NotNull(message = "invalid parameter - gameMode")
        GameMode gameMode,

        @NotNull(message = "invalid parameter - gameDurationMinutes")
        Integer gameDurationMinutes,

        Long    reserveNo,

        @NotNull
        Integer shopNo
) {}