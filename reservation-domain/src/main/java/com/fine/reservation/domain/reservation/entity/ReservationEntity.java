package com.fine.reservation.domain.reservation.entity;

import com.fine.reservation.domain.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservation")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ReservationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reserve_no", nullable = false)
    private Long reserveNo;

    @Column(name = "shop_no")
    private Integer shopNo;

    @Column(name = "usr_no")
    private Integer usrNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "reserve_status")
    private ReservationStatus reserveStatus;

    @Column(name = "reserve_datetime")
    private LocalDateTime reserveDatetime;

    @Column(name = "reserve_name", length = 300)
    private String reserveName;

    @Column(name = "reserve_phone_number", length = 300)
    private String reservePhoneNumber;

    @Column(name = "reserve_software")
    private Integer reserveSoftware;

    @Column(name = "reserve_people")
    private Integer reservePeople;

    @Column(name = "reserve_request_message", length = 500)
    private String reserveRequestMessage;

    @Column(name = "regist_date")
    private LocalDateTime registDate;

    @Column(name = "modify_date")
    private LocalDateTime modifyDate;

    @Column(name = "reserve_cancel_reason")
    private Integer reserveCancelReason;

    @Column(name = "alarm_status")
    private Integer alarmStatus;

    @Column(name = "autocall_status")
    private Integer autocallStatus;

    @Column(name = "reserve_name_mask", length = 300)
    private String reserveNameMask;

    @Column(name = "reserve_phone_number_mask", length = 300)
    private String reservePhoneNumberMask;

    @Column(name = "booking_name", length = 100)
    private String bookingName;

    @Column(name = "booking_phone_number", length = 20)
    private String bookingPhoneNumber;

    @Column(name = "reserve_datetime_end")
    private LocalDateTime reserveDatetimeEnd;

    @Column(name = "user_id", length = 20)
    private String userId;

    @Column(name = "nickname", length = 24)
    private String nickname;

    @Column(name = "sex", length = 1)
    private String sex;

    @Column(name = "game_type")
    private Integer gameType;

    @Column(name = "room_count")
    private Integer roomCount;

    @Column(name = "game_count")
    private Integer gameCount;

    @Column(name = "option_flag")
    private Integer optionFlag;

    @Column(name = "is_self_reserve")
    private Integer isSelfReserve;

    @Column(name = "is_self_regist")
    private Integer isSelfRegist;
}