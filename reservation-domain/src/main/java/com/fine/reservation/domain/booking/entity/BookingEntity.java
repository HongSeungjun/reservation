package com.fine.reservation.domain.booking.entity;

import com.fine.reservation.domain.enums.BookingChannel;
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
    @Column(name = "booking_no", nullable = false)
    private Long bookingNo;

    @Column(name = "shop_no", nullable = false)
    private Integer shopNo;

    @Column(name = "machine_no", nullable = false)
    private Integer machineNo;

    @Column(name = "booker_name", length = 300, nullable = false)
    private String bookerName;

    @Column(name = "phone_number", length = 300, nullable = false)
    private String phoneNumber;

    @Column(name = "people_count", nullable = false)
    private Integer peopleCount;

    @Column(name = "hole_count", nullable = false)
    private Integer holeCount;

    @Column(name = "booking_memo", length = 1000)
    private String bookingMemo;

    @Column(name = "booking_start_at", nullable = false)
    private LocalDateTime bookingStartAt;

    @Column(name = "booking_end_at", nullable = false)
    private LocalDateTime bookingEndAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "reserve_no", nullable = false)
    private Long reserveNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "booking_channel", nullable = false)
    private BookingChannel bookingChannel;

    @Column(name = "game_mode", nullable = false)
    private Integer gameMode;

    @Column(name = "game_duration_minutes", nullable = false)
    private Integer gameDurationMinutes;

}
