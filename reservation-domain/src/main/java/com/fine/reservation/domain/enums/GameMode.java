package com.fine.reservation.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum GameMode {
    STROKE(0, 0, "A300", "A301", "스트로크"),
    MATCH(1, 1, "A300", "A302", "매치"),
    SKINS(2, 2, "A300", "A303", "스킨스"),
    LONG_HIT(3, 3, "A300", "A304", "장타"),
    NEAR_PIN(4, 4, "A300", "A305", "니어핀"),
    PUTTING(5, 5, "A300", "A306", "퍼팅"),
    PRACTICE(6, 6, "A300", "A307", "연습장"),
    STABLEFORD(7, 7, "A300", "A308", "스테이블포드"),
    NEW_PERIO(8, 8, "A300", "A309", "신페리오"),
    LAS_VEGAS(9, 9, "A300", "A310", "라스베가스"),
    FOURSOMES(10, 10, "A300", "A311", "포섬"),
    CHIP_AND_PUTT(11, 11, "A300", "A312", "칩앤펏"),
    MINI_ROUND(13, 13, "A300", "A313", "미니라운드"),
    PAR3_CHALLENGE(14, 14, "A300", "A314", "PAR3 챌린지");

    private final int key;
    private final int value;
    private final String parentCode;
    private final String commonCode;
    private final String description;

    public static GameMode fromValue(int value) {
        return Arrays.stream(values())
                .filter(mode -> mode.value == value)
                .findFirst()
                .orElse(null);
    }

}