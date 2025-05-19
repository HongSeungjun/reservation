package com.fine.reservation.domain.booking.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "booking_schedule")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class BookingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_seq", nullable = false)
    private Long bookingSeq;

    @Column(name = "rgn_no")
    private Integer rgnNo;

    @Column(name = "work_no")
    private Integer workNo;

    @Column(name = "name", length = 300)
    private String name;

    @Column(name = "phone", length = 300)
    private String phone;

    @Column(name = "player_cnt")
    private Integer playerCnt;

    @Column(name = "play_hole")
    private Integer playHole;

    @Column(name = "reg_nm", length = 20)
    private String regNm;

    @Column(name = "memo", length = 1000)
    private String memo;

    @Column(name = "booking_date")
    private LocalDateTime bookingDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "reg_dt")
    private LocalDateTime regDt;

    @Column(name = "mod_dt")
    private LocalDateTime modDt;

    @Column(name = "reserve_no")
    private Long reserveNo;

    @Column(name = "booking_channel")
    private Integer bookingChannel;

    @Column(name = "search_phone", length = 4)
    private String searchPhone;

    @Column(name = "game_mode")
    private Integer gameMode;

    @Column(name = "game_time")
    private Integer gameTime;

    @Column(name = "booking_name", length = 100)
    private String bookingName;

    @Column(name = "booking_phone_number", length = 20)
    private String bookingPhoneNumber;

    @Column(name = "first_booking_date")
    private LocalDateTime firstBookingDate;

    @Column(name = "first_booking_end_date")
    private LocalDateTime firstBookingEndDate;

    @Column(name = "fixed_yn", length = 1)
    private String fixedYn;

    @Column(name = "alarm_status")
    private Integer alarmStatus;
}