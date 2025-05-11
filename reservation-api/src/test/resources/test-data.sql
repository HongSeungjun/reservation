DELETE FROM booking_schedule;
DELETE FROM reservation;

-- 1) REQUEST 상태 예약
INSERT INTO reservation (
    reserve_no, shop_no, usr_no, reserve_status,
    reserve_datetime, reserve_datetime_end, reserve_name,
    reserve_phone_number, reserve_software, reserve_people,
    reserve_request_message, regist_date, modify_date
) VALUES (
             1, 101, 1001, 'REQUEST',
             DATEADD('HOUR', 1, CURRENT_TIMESTAMP), DATEADD('HOUR', 3, CURRENT_TIMESTAMP),
             '홍길동', '010-1234-5678', 1, 4,
             '테스트 예약입니다.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
         );

-- 2) APPROVAL 상태 예약 (재승인 불가 케이스)
INSERT INTO reservation (
    reserve_no, shop_no, usr_no, reserve_status,
    reserve_datetime, reserve_datetime_end, reserve_name,
    reserve_phone_number, reserve_software, reserve_people,
    reserve_request_message, regist_date, modify_date
) VALUES (
             2, 101, 1002, 'APPROVAL',
             DATEADD('HOUR', 2, CURRENT_TIMESTAMP), DATEADD('HOUR', 4, CURRENT_TIMESTAMP),
             '이영희', '010-5555-6666', 1, 2,
             '승인된 예약 테스트', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
         );