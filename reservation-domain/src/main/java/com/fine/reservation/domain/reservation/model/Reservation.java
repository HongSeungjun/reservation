package com.fine.reservation.domain.reservation.model;

import com.fine.reservation.domain.enums.ReservationStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString
public class Reservation {
    private Integer reserveNo;
    private Long shopNo;
    private Integer usrNo;
    private ReservationStatus reserveStatus;
    private LocalDateTime reserveStartAt;
    private LocalDateTime reserveEndAt;
    private String reserveName;
    private String reservePhoneNumber;
    private Integer reserveSoftware;
    private Integer reservePeople;
    private String reserveRequestMessage;
    private LocalDateTime createdAt;

    private String usrId;
    private String usrNickName;
    private String usrGhandy;
    private String usrGrade;
    private String sex;
    private Long noticeNo;

    private LocalDate lastRoundDate;
    private Integer roundCount3months;
    private String hashTag;
    private String crmMemo;
    private String siteConfirmYn;
    private Float round1Month;
    private Float avgRoundTime;

    private String bookingName;
    private String bookingPhoneNumber;

    private Integer gameType;
    private Integer roomCount;
    private Integer gameCount;
    private Integer optionFlag;

    private Long[] machineNos;
    private String searchPhone;
    private Integer result;
    private Integer firstBookingSeq;
    private List<Integer> bookingNos;

    public Reservation withPhoneNumber(String phone) {
        return this.toBuilder()
                .bookingPhoneNumber(phone)
                .build();
    }

    public Reservation withSearchPhone() {
        String pattern = "(\\d{3})[-]?(\\d{3,4})[-]?(\\d{4})";
        String sp = null;
        if (this.reservePhoneNumber != null && this.reservePhoneNumber.matches(pattern)) {
            sp = this.reservePhoneNumber.substring(this.reservePhoneNumber.length() - 4);
        }
        return this.toBuilder()
                .searchPhone(sp)
                .build();
    }
}