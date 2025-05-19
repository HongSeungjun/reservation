package com.fine.reservation.domain.reservation.entity;

import com.fine.reservation.domain.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.annotation.LastModifiedDate;

@Entity
@Table(name = "reservation")
@EntityListeners(AuditingEntityListener.class)
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

    @Column(name = "user_no")
    private Integer userNo;

    @Enumerated(EnumType.STRING)
    @Column(name = "reserve_status")
    private ReservationStatus reserveStatus;

    @Column(name = "reserve_datetime", nullable = false)
    private LocalDateTime reservationStartAt;

    @Column(name = "reserve_name", length = 300, nullable = false)
    private String reserverName;

    @Column(name = "reserve_phone_number", length = 300, nullable = false)
    private String reserverPhoneNumber;

    @Column(name = "reserve_software")
    private Integer reserveSoftware;

    @Column(name = "reserve_people", nullable = false)
    private Integer peopleCount;

    @Column(name = "reserve_request_message", length = 500)
    private String requestMessage;

    @Column(name = "regist_date", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "reserve_cancel_reason")
    private Integer reserveCancelReason;


    @Column(name = "booking_name", length = 100)
    private String bookingName;

    @Column(name = "booking_phone_number", length = 20)
    private String bookingPhoneNumber;

    @Column(name = "reserve_datetime_end", nullable = false)
    private LocalDateTime reservationEndAt;

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

    public ReservationEntity approve() {
        if (this.reserveStatus != ReservationStatus.REQUEST) {
            throw new IllegalStateException("예약 승인을 할수 없습니다." + this.reserveStatus);
        }
        this.reserveStatus = ReservationStatus.APPROVAL;
        return this;
    }

}

