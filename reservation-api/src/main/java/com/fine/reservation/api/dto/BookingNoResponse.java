package com.fine.reservation.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class BookingNoResponse {
    private final List<Long> bookingNo;
}