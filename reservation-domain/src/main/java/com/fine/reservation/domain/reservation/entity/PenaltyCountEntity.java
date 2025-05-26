package com.fine.reservation.domain.reservation.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "penalty_count", schema = "dbo")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PenaltyCountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "penalty_count_id")
    private Long id;

    @Column(name = "user_no", nullable = false)
    private Integer userNo;

    @Column(name = "total_penalty", nullable = false)
    private Integer totalPenalty;

    @Column(name = "start_at", nullable = false)
    private LocalDateTime startAt;

    @Column(name = "end_at", nullable = false)
    private LocalDateTime endAt;


    @Builder
    public PenaltyCountEntity(Integer userNo, Integer totalPenalty, LocalDateTime startAt, LocalDateTime endAt) {
        this.userNo = userNo;
        this.totalPenalty = totalPenalty;
        this.startAt = startAt;
        this.endAt = endAt;
    }
}