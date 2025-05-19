package com.fine.reservation.domain.booking.model;

import com.fine.reservation.domain.enums.BookingChannel;
import com.fine.reservation.domain.enums.GameMode;
import com.fine.reservation.domain.enums.ReservationStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

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

    public Booking withMachineNo(Long machineNo) {
        return this.toBuilder()
            .machineNo(machineNo)
            .build();
    }

    public Booking withSearchPhone() {
        String pattern = "(\\d{3})[-]?(\\d{3,4})[-]?(\\d{4})";
        String sp = null;
        if (this.cellNumber != null && this.cellNumber.matches(pattern)) {
            sp = this.cellNumber.substring(this.cellNumber.length() - 4);
        }
        return this.toBuilder()
            .searchPhone(sp)
            .build();
    }
}