-- Oracle Database Initial Data for Movie Service
-- 영화 예매 시스템 기본 데이터 (Spring Boot 자동 실행용)

-- ==========================================
-- 기본 데이터 삽입 (Initial Data)
-- DataInitializer의 내용을 SQL로 변환
-- ==========================================

-- 고객 등급 기본 데이터
INSERT INTO client_level (level_id, level_name, reward_rate) VALUES (1, 'BASIC', 0.00);
INSERT INTO client_level (level_id, level_name, reward_rate) VALUES (2, 'BRONZE', 0.02);
INSERT INTO client_level (level_id, level_name, reward_rate) VALUES (3, 'SILVER', 0.03);
INSERT INTO client_level (level_id, level_name, reward_rate) VALUES (4, 'GOLD', 0.04);
INSERT INTO client_level (level_id, level_name, reward_rate) VALUES (5, 'DIAMOND', 0.05);

-- 기본 쿠폰 데이터
INSERT INTO coupon (coupon_id, coupon_name, coupon_description, start_date, end_date, discount_amount) 
VALUES (coupon_seq.NEXTVAL, '생일쿠폰', '생일 축하 특별 할인쿠폰', DATE '2024-01-01', DATE '2030-12-31', 2000);

INSERT INTO coupon (coupon_id, coupon_name, coupon_description, start_date, end_date, discount_amount) 
VALUES (coupon_seq.NEXTVAL, '신규가입쿠폰', '신규 회원가입 환영 쿠폰', SYSDATE, ADD_MONTHS(SYSDATE, 3), 2000);

INSERT INTO coupon (coupon_id, coupon_name, coupon_description, start_date, end_date, discount_amount) 
VALUES (coupon_seq.NEXTVAL, '등급업쿠폰', '등급 업그레이드 축하 쿠폰', SYSDATE, ADD_MONTHS(SYSDATE, 3), 2000);

INSERT INTO coupon (coupon_id, coupon_name, coupon_description, start_date, end_date, discount_amount) 
VALUES (coupon_seq.NEXTVAL, '특별할인쿠폰', '특별 이벤트 할인 쿠폰', SYSDATE, ADD_MONTHS(SYSDATE, 3), 2000);

-- 기본 장르 데이터
INSERT INTO genre (genre_id, genre_name) VALUES (genre_seq.NEXTVAL, '액션');
INSERT INTO genre (genre_id, genre_name) VALUES (genre_seq.NEXTVAL, '코미디');
INSERT INTO genre (genre_id, genre_name) VALUES (genre_seq.NEXTVAL, '드라마');
INSERT INTO genre (genre_id, genre_name) VALUES (genre_seq.NEXTVAL, '로맨스');
INSERT INTO genre (genre_id, genre_name) VALUES (genre_seq.NEXTVAL, '스릴러');
INSERT INTO genre (genre_id, genre_name) VALUES (genre_seq.NEXTVAL, '호러');
INSERT INTO genre (genre_id, genre_name) VALUES (genre_seq.NEXTVAL, 'SF');
INSERT INTO genre (genre_id, genre_name) VALUES (genre_seq.NEXTVAL, '판타지');
INSERT INTO genre (genre_id, genre_name) VALUES (genre_seq.NEXTVAL, '애니메이션');
INSERT INTO genre (genre_id, genre_name) VALUES (genre_seq.NEXTVAL, '다큐멘터리');
INSERT INTO genre (genre_id, genre_name) VALUES (genre_seq.NEXTVAL, '범죄');
INSERT INTO genre (genre_id, genre_name) VALUES (genre_seq.NEXTVAL, '전쟁');
INSERT INTO genre (genre_id, genre_name) VALUES (genre_seq.NEXTVAL, '음악');
INSERT INTO genre (genre_id, genre_name) VALUES (genre_seq.NEXTVAL, '가족');
INSERT INTO genre (genre_id, genre_name) VALUES (genre_seq.NEXTVAL, '미스터리');

-- 기본 배우 데이터
INSERT INTO actor (actor_id, actor_name, birth_date) VALUES (actor_seq.NEXTVAL, '송강호', DATE '1967-01-17');
INSERT INTO actor (actor_id, actor_name, birth_date) VALUES (actor_seq.NEXTVAL, '전도연', DATE '1973-02-11');
INSERT INTO actor (actor_id, actor_name, birth_date) VALUES (actor_seq.NEXTVAL, '이병헌', DATE '1970-07-12');
INSERT INTO actor (actor_id, actor_name, birth_date) VALUES (actor_seq.NEXTVAL, '김혜수', DATE '1970-09-05');
INSERT INTO actor (actor_id, actor_name, birth_date) VALUES (actor_seq.NEXTVAL, '박서준', DATE '1988-12-16');
INSERT INTO actor (actor_id, actor_name, birth_date) VALUES (actor_seq.NEXTVAL, '수지', DATE '1994-10-10');
INSERT INTO actor (actor_id, actor_name, birth_date) VALUES (actor_seq.NEXTVAL, '마동석', DATE '1971-03-01');
INSERT INTO actor (actor_id, actor_name, birth_date) VALUES (actor_seq.NEXTVAL, '윤여정', DATE '1947-06-19');
INSERT INTO actor (actor_id, actor_name, birth_date) VALUES (actor_seq.NEXTVAL, '이정재', DATE '1973-03-15');
INSERT INTO actor (actor_id, actor_name, birth_date) VALUES (actor_seq.NEXTVAL, '박보영', DATE '1990-02-12');

-- 기본 극장 데이터
INSERT INTO theater (theater_id, theater_name, total_seats, row_count, column_count) 
VALUES (theater_seq.NEXTVAL, 'CGV 강남', 150, 10, 15);

INSERT INTO theater (theater_id, theater_name, total_seats, row_count, column_count) 
VALUES (theater_seq.NEXTVAL, 'CGV 홍대', 120, 8, 15);

INSERT INTO theater (theater_id, theater_name, total_seats, row_count, column_count) 
VALUES (theater_seq.NEXTVAL, '롯데시네마 월드타워', 200, 12, 17);

INSERT INTO theater (theater_id, theater_name, total_seats, row_count, column_count) 
VALUES (theater_seq.NEXTVAL, '메가박스 코엑스', 180, 10, 18);

INSERT INTO theater (theater_id, theater_name, total_seats, row_count, column_count) 
VALUES (theater_seq.NEXTVAL, 'CGV 용산아이파크몰', 160, 10, 16);

-- 샘플 영화 데이터
INSERT INTO movie (movie_id, view_rating, movie_name, running_time, director_name, movie_desc, distributor, release_date, end_date, coo) 
VALUES (movie_seq.NEXTVAL, '12세', '기생충', 132, '봉준호', '반지하에 살면서 돈 냄새를 풍기는 기택 가족에게 찾아온 일생일대의 기회', 'CJ엔터테인먼트', DATE '2019-05-30', DATE '2025-12-31', '한국');

INSERT INTO movie (movie_id, view_rating, movie_name, running_time, director_name, movie_desc, distributor, release_date, end_date, coo) 
VALUES (movie_seq.NEXTVAL, '15세', '미나리', 115, '정이삭', '더 나은 삶을 찾아 아칸소로 이주한 한국 가족의 이야기', '판씨네마', DATE '2021-03-03', DATE '2025-12-31', '미국');

INSERT INTO movie (movie_id, view_rating, movie_name, running_time, director_name, movie_desc, distributor, release_date, end_date, coo) 
VALUES (movie_seq.NEXTVAL, '12세', '오징어 게임', 125, '황동혁', '456명이 참가한 생존 게임의 이야기', 'Netflix', DATE '2021-09-17', DATE '2025-12-31', '한국');

-- 테스트용 관리자 계정
INSERT INTO customer (customer_id, customer_input_id, customer_pw, customer_name, birth_date, phone, authority, join_date, points, level_id)
VALUES (customer_seq.NEXTVAL, 'admin', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '관리자', DATE '1990-01-01', '010-0000-0000', 'A', SYSDATE, 0, 5);

-- 테스트용 일반 사용자
INSERT INTO customer (customer_id, customer_input_id, customer_pw, customer_name, birth_date, phone, authority, join_date, points, level_id)
VALUES (customer_seq.NEXTVAL, 'user1', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '김영화', DATE '1995-05-15', '010-1234-5678', 'M', SYSDATE, 1000, 2); 