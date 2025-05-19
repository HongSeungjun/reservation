-- 초기 데이터 삭제
DELETE FROM booking_schedule;
DELETE FROM reservation;

-- 1) REQUEST 상태 예약 (reserve_no=1)
INSERT INTO reservation (
    reserve_no,
    shop_no,
    user_no,
    reserve_status,
    reserve_datetime,
    reserve_datetime_end,
    reserve_name,
    reserve_phone_number,
    reserve_software,
    reserve_people,
    reserve_request_message,
    regist_date,
    updated_at
) VALUES (
             1,              -- reserve_no
             101,            -- shop_no
             1001,           -- user_no
             'REQUEST',      -- reserve_status
             DATEADD('HOUR', 1, CURRENT_TIMESTAMP),  -- reserve_datetime
             DATEADD('HOUR', 3, CURRENT_TIMESTAMP),  -- reserve_datetime_end
             '홍길동',        -- reserve_name
             '010-1234-5678',-- reserve_phone_number
             1,              -- reserve_software
             4,              -- reserve_people
             '테스트 예약입니다.', -- reserve_request_message
             CURRENT_TIMESTAMP,   -- regist_date
             CURRENT_TIMESTAMP    -- updated_at
         );

-- 2) APPROVAL 상태 예약 (reserve_no=2)
INSERT INTO reservation (
    reserve_no,
    shop_no,
    user_no,
    reserve_status,
    reserve_datetime,
    reserve_datetime_end,
    reserve_name,
    reserve_phone_number,
    reserve_software,
    reserve_people,
    reserve_request_message,
    regist_date,
    updated_at
) VALUES (
             2,
             101,
             1002,
             'APPROVAL',
             DATEADD('HOUR', 2, CURRENT_TIMESTAMP),
             DATEADD('HOUR', 4, CURRENT_TIMESTAMP),
             '이영희',
             '010-5555-6666',
             1,
             2,
             '승인된 예약 테스트',
             CURRENT_TIMESTAMP,
             CURRENT_TIMESTAMP
         );