-- 초기 데이터 삭제
DELETE FROM booking_schedule;
DELETE FROM reservation;

-- 1) REQUEST 상태 예약 (reserve_no=1) - POST /bookings 테스트용 (예약 승인 및 Booking 생성)
INSERT INTO reservation (
    reserve_no, shop_no, user_no, reserve_status, reserve_datetime, reserve_datetime_end,
    reserve_name, reserve_phone_number, reserve_software, reserve_people, reserve_request_message,
    regist_date, updated_at
) VALUES (
             1, 101, 1001, 'REQUEST',
             DATEADD('HOUR', 1, CURRENT_TIMESTAMP), DATEADD('HOUR', 3, CURRENT_TIMESTAMP),
             '홍길동', '010-1234-5678', 1, 4, 'REQUEST 상태 예약 테스트',
             CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
         );

-- 2) APPROVAL 상태 예약 (reserve_no=2) - POST /bookings 테스트용 (이미 승인된 예약) 및 여기에 연결된 booking_schedule 데이터 생성
INSERT INTO reservation (
    reserve_no, shop_no, user_no, reserve_status, reserve_datetime, reserve_datetime_end,
    reserve_name, reserve_phone_number, reserve_software, reserve_people, reserve_request_message,
    regist_date, updated_at
) VALUES (
             2, 101, 1002, 'APPROVAL',
             DATEADD('HOUR', 2, CURRENT_TIMESTAMP), DATEADD('HOUR', 4, CURRENT_TIMESTAMP), -- 예약 시간: 현재로부터 2시간 뒤 ~ 4시간 뒤
             '이영희', '010-5555-6666', 1, 2, 'APPROVAL 상태 예약 테스트',
             CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
         );

INSERT INTO booking_schedule (
    booking_no, shop_no, machine_no, -- PK 컬럼명이 booking_no가 맞는지 확인해주세요.
    booking_start_at, booking_end_at,
    people_count, hole_count, booker_name, phone_number,
    booking_channel, game_mode, game_duration_minutes,
    reserve_no, booking_memo, created_at, updated_at
) VALUES (
             201, 101, 1, -- booking_no, shop_no, machine_no
             DATEADD('HOUR', 2, CURRENT_TIMESTAMP), DATEADD('HOUR', 4, CURRENT_TIMESTAMP), -- booking_start_at, booking_end_at (reserve_no=2와 일치)
             2, 18, '이영희', '010-5555-6666', -- people_count, hole_count, booker_name, phone_number
             'MOBILE', 'STROKE', 120, -- booking_channel, game_mode, game_duration_minutes
             2, '승인된 예약 - 기기1', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP -- reserve_no, booking_memo, created_at, updated_at
         ), (
             202, 101, 2, -- booking_no, shop_no, machine_no
             DATEADD('HOUR', 2, CURRENT_TIMESTAMP), DATEADD('HOUR', 4, CURRENT_TIMESTAMP), -- booking_start_at, booking_end_at (reserve_no=2와 일치)
             2, 18, '이영희', '010-5555-6666', -- people_count, hole_count, booker_name, phone_number
             'MOBILE', 'STROKE', 120, -- booking_channel, game_mode, game_duration_minutes
             2, '승인된 예약 - 기기2', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP -- reserve_no, booking_memo, created_at, updated_at
         );

-- booking_schedule_no=301: reserve_no 없이 생성된 단독 예약 스케줄 - PUT /bookings/{bookingNo} 테스트 대상
INSERT INTO booking_schedule (
    booking_no, shop_no, machine_no, -- PK 컬럼명이 booking_no가 맞는지 확인해주세요.
    booking_start_at, booking_end_at,
    people_count, hole_count, booker_name, phone_number,
    booking_channel, game_mode, game_duration_minutes,
    reserve_no, booking_memo, created_at, updated_at
) VALUES (
             301, 101, 3, -- booking_no, shop_no, machine_no
             DATEADD('HOUR', 5, CURRENT_TIMESTAMP), DATEADD('HOUR', 6, CURRENT_TIMESTAMP), -- booking_start_at, booking_end_at
             4, 18, '김단독', '010-3333-3333', -- people_count, hole_count, booker_name, phone_number
             'MANUAL', 'STROKE', 60, -- booking_channel, game_mode, game_duration_minutes
             NULL, '단독 수동 예약', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP -- reserve_no, booking_memo, created_at, updated_at
         );

-- booking_schedule_no=401: 시간 중복/충돌 테스트를 위해 사용될 수 있는 예약 스케줄
INSERT INTO booking_schedule (
    booking_no, shop_no, machine_no, -- PK 컬럼명이 booking_no가 맞는지 확인해주세요.
    booking_start_at, booking_end_at,
    people_count, hole_count, booker_name, phone_number,
    booking_channel, game_mode, game_duration_minutes,
    reserve_no, booking_memo, created_at, updated_at
) VALUES (
             401, 101, 4, -- booking_no, shop_no, machine_no
             DATEADD('DAY', 1, parsedatetime('14:00:00', 'HH:mm:ss')), DATEADD('DAY', 1, parsedatetime('15:00:00', 'HH:mm:ss')), -- booking_start_at, booking_end_at (내일 특정 시간)
             2, 18, '박충돌', '010-4444-4444', -- people_count, hole_count, booker_name, phone_number
             'MANUAL', 'STROKE', 60, -- booking_channel, game_mode, game_duration_minutes
             NULL, '충돌 테스트용 예약', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP -- reserve_no, booking_memo, created_at, updated_at
         );