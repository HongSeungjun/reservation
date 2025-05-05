package com.fine.reservation.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum BookingChannel {
    ALL(0, "0", "전체"),
    MANUAL(1, "1", "수동"),
    MOBILE(2, "2", "모바일"),
    KIOSK(3, "3", "키오스크"),
    CID(4, "4", "cid");

    private final int key;
    private final String value;
    private final String description;

    public static BookingChannel findByValue(int value) {
        return Arrays.stream(values())
            .filter(f -> f.value.equals(String.valueOf(value)))
            .findAny()
            .orElse(null);
    }
}