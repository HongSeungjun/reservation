package com.fine.reservation.api.dto;

import com.fine.reservation.domain.enums.BookingChannel;
import com.fine.reservation.domain.enums.ReservationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;


@Getter
@Builder
public class BookingRequest {
    @NotNull(message = "invalid parameter - machineNos")
    private Long[] machineNos;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "invalid parameter - bookingStartAt")
    private String bookingStartAt;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(message = "invalid parameter - bookingEndAt")
    private String bookingEndAt;

    @NotNull(message = "invalid parameter - bookingPeople")
    @Range(min = 1, max = 6, message = "bookingPeople min 1 max 6")
    private Integer bookingPeople;

    @NotNull(message = "invalid parameter - bookingPlayHole")
    private Integer bookingPlayHole;

    @NotNull(message = "invalid parameter - bookingName")
    private String bookingName;

    @NotNull(message = "invalid parameter - cellNumber")
    private String cellNumber;

    @NotNull(message = "invalid parameter - bookingChannel")
    private BookingChannel bookingChannel;

    @NotNull(message = "invalid parameter - gameMode")
    private Integer gameMode;

    private Integer gameTime;
    private String bookingMemo;

    private Long reserveNo;

    private ReservationStatus reserveStatus;
}