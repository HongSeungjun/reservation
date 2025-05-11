package com.fine.reservation.api.dto;

import com.fine.reservation.domain.enums.BookingChannel;
import com.fine.reservation.domain.enums.GameMode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodayBookingResponse {
    private Long bookingNo;
    private Long machineNo;
    private LocalDateTime bookingStartAt;
    private LocalDateTime bookingEndAt;
    private Integer bookingPeople;
    private Integer bookingPlayHole;
    private String bookingName;
    private String cellNumber;
    private BookingChannel bookingChannel;
    private GameMode gameMode;
    private Integer gameTime;
}