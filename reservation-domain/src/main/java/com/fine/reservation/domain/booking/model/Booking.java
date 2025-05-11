package com.fine.reservation.domain.booking.model;

import com.fine.reservation.domain.enums.BookingChannel;
import com.fine.reservation.domain.enums.GameMode;
import com.fine.reservation.domain.enums.ReservationStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class Booking {
    private Long bookingNo;
    private Long shopNo;
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
    private String bookingMemo;
    private Long reserveNo;
    private ReservationStatus reserveStatus;

    private Long[] machineNos;
    private String searchPhone;
    private Integer result;
    private Integer firstBookingSeq;

    public Booking changeMachine(Long newMachineNo) {
        return this.toBuilder()
                .machineNo(newMachineNo)
                .build();
    }

    public boolean isValidBookingTime() {
        if (bookingStartAt == null || bookingEndAt == null) {
            return false;
        }

        return bookingStartAt.isBefore(bookingEndAt) &&
                !bookingStartAt.isBefore(LocalDateTime.now());
    }

    public boolean isWithinMaxPeople() {
        final int MAX_PEOPLE = 6;
        return bookingPeople != null && bookingPeople > 0 && bookingPeople <= MAX_PEOPLE;
    }

    public boolean isValidTimeRange() {
        if (bookingStartAt == null || bookingEndAt == null) {
            return false;
        }

        long minutes = ChronoUnit.MINUTES.between(bookingStartAt, bookingEndAt);
        return minutes >= 30 && minutes <= 240;

    }

    public Booking copyWithNewMachine(Long newMachineNo) {
        return this.toBuilder()
                .bookingNo(null)
                .machineNo(newMachineNo)
                .build();
    }




}