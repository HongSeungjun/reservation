package com.fine.reservation.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum ReservationStatus {
    REQUEST(10, "10", "신청"),
    APPROVAL(20, "20", "승인"),
    CUSTOMER_CANCEL_BEFORE_APPROVAL(31, "31", "승인전고객취소"),
    STORE_CANCEL_BEFORE_APPROVAL(32, "32", "승인전매장취소"),
    AUTO_CANCEL(33, "33", "자동취소"),
    CUSTOMER_CANCEL_AFTER_APPROVAL(34, "34", "승인후고객취소"),
    STORE_CANCEL_AFTER_APPROVAL(35, "35", "승인후매장취소"),
    NO_SHOW_AFTER_APPROVAL(36, "36", "승인후NoShow"),
    COMPLETED_USE(40, "40", "이용완료");

    private final int key;
    private final String value;
    private final String description;

    public static ReservationStatus findByValue(int value) {
        return Arrays.stream(values())
            .filter(s -> s.value.equals(String.valueOf(value)))
            .findAny()
            .orElse(null);
    }

    public boolean canTransitionTo(ReservationStatus next) {
        switch (this) {
            case REQUEST:
                return next == APPROVAL
                    || next == CUSTOMER_CANCEL_BEFORE_APPROVAL
                    || next == STORE_CANCEL_BEFORE_APPROVAL;
            case APPROVAL:
                return next == CUSTOMER_CANCEL_AFTER_APPROVAL
                    || next == STORE_CANCEL_AFTER_APPROVAL
                    || next == NO_SHOW_AFTER_APPROVAL
                    || next == AUTO_CANCEL
                    || next == COMPLETED_USE;
            default:
                return false;
        }
    }
}