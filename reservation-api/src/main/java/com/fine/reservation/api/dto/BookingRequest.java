package com.fine.reservation.api.dto;

import com.fine.reservation.domain.enums.BookingChannel;
import com.fine.reservation.domain.enums.GameMode;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.List;


/*
* builder 패턴 제거하고 생성자, 불변 객체를 이용
* */
public record BookingRequest(
        @NotNull(message = "invalid parameter - machineNos")
        List<Integer> machineNos,

        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @NotNull(message = "invalid parameter - bookingStartAt")
        String bookingStartAt,

        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @NotNull(message = "invalid parameter - bookingEndAt")
        String bookingEndAt,

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