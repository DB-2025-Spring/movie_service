-- Oracle Database Schema for Movie Service
-- 영화 예매 시스템 데이터베이스 스키마 (Spring Boot 자동 실행용)

-- ==========================================
-- 시퀀스 생성 (Sequences)
-- ==========================================

-- 고객 레벨 시퀀스 (ClientLevel은 수동 ID이므로 제외)
CREATE SEQUENCE customer_seq START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE actor_seq START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE genre_seq START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE movie_seq START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE theater_seq START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE seat_seq START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE schedule_seq START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE coupon_seq START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE issue_coupon_seq START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE payment_seq START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE ticket_seq START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE SEQUENCE review_seq START WITH 1 INCREMENT BY 1 NOCACHE;

-- ==========================================
-- 테이블 생성 (Tables)
-- ==========================================

-- 1. 고객 등급 테이블 (CLIENT_LEVEL)
CREATE TABLE client_level (
    level_id NUMBER(10) NOT NULL,
    level_name VARCHAR2(50) NOT NULL,
    reward_rate NUMBER(5,2),
    CONSTRAINT pk_client_level PRIMARY KEY (level_id)
);

-- 2. 고객 테이블 (CUSTOMER)
CREATE TABLE customer (
    customer_id NUMBER(10) NOT NULL,
    customer_input_id VARCHAR2(100) NOT NULL,
    customer_pw VARCHAR2(100) NOT NULL,
    customer_name VARCHAR2(100) NOT NULL,
    birth_date DATE,
    phone VARCHAR2(20),
    authority VARCHAR2(1),
    join_date DATE,
    points NUMBER(10),
    level_id NUMBER(10) NOT NULL,
    CONSTRAINT pk_customer PRIMARY KEY (customer_id),
    CONSTRAINT fk_customer_level FOREIGN KEY (level_id) REFERENCES client_level(level_id),
    CONSTRAINT uk_customer_input_id UNIQUE (customer_input_id)
);

-- 3. 배우 테이블 (ACTOR)
CREATE TABLE actor (
    actor_id NUMBER(10) NOT NULL,
    actor_name VARCHAR2(50),
    birth_date DATE,
    CONSTRAINT pk_actor PRIMARY KEY (actor_id)
);

-- 4. 장르 테이블 (GENRE)
CREATE TABLE genre (
    genre_id NUMBER(10) NOT NULL,
    genre_name VARCHAR2(100),
    CONSTRAINT pk_genre PRIMARY KEY (genre_id)
);

-- 5. 영화 테이블 (MOVIE)
CREATE TABLE movie (
    movie_id NUMBER(10) NOT NULL,
    view_rating VARCHAR2(10),
    movie_name VARCHAR2(100) NOT NULL,
    running_time NUMBER(10),
    director_name VARCHAR2(50),
    movie_desc CLOB,
    distributor VARCHAR2(100),
    image_url VARCHAR2(500),
    release_date DATE,
    end_date DATE,
    coo VARCHAR2(100),
    CONSTRAINT pk_movie PRIMARY KEY (movie_id)
);

-- 6. 영화-배우 연결 테이블 (MOVIE_ACTOR)
CREATE TABLE movie_actor (
    movie_id NUMBER(10) NOT NULL,
    actor_id NUMBER(10) NOT NULL,
    CONSTRAINT pk_movie_actor PRIMARY KEY (movie_id, actor_id),
    CONSTRAINT fk_movie_actor_movie FOREIGN KEY (movie_id) REFERENCES movie(movie_id),
    CONSTRAINT fk_movie_actor_actor FOREIGN KEY (actor_id) REFERENCES actor(actor_id)
);

-- 7. 영화-장르 연결 테이블 (MOVIE_GENRE)
CREATE TABLE movie_genre (
    movie_id NUMBER(10) NOT NULL,
    genre_id NUMBER(10) NOT NULL,
    CONSTRAINT pk_movie_genre PRIMARY KEY (movie_id, genre_id),
    CONSTRAINT fk_movie_genre_movie FOREIGN KEY (movie_id) REFERENCES movie(movie_id),
    CONSTRAINT fk_movie_genre_genre FOREIGN KEY (genre_id) REFERENCES genre(genre_id)
);

-- 8. 극장 테이블 (THEATER)
CREATE TABLE theater (
    theater_id NUMBER(10) NOT NULL,
    theater_name VARCHAR2(100) NOT NULL,
    total_seats NUMBER(10) NOT NULL,
    row_count NUMBER(10),
    column_count NUMBER(10),
    CONSTRAINT pk_theater PRIMARY KEY (theater_id)
);

-- 9. 좌석 테이블 (SEAT)
CREATE TABLE seat (
    seat_id NUMBER(10) NOT NULL,
    theater_id NUMBER(10) NOT NULL,
    row_number NUMBER(10) NOT NULL,
    column_number NUMBER(10) NOT NULL,
    CONSTRAINT pk_seat PRIMARY KEY (seat_id),
    CONSTRAINT fk_seat_theater FOREIGN KEY (theater_id) REFERENCES theater(theater_id)
);

-- 10. 상영 일정 테이블 (SCHEDULE)
CREATE TABLE schedule (
    schedule_id NUMBER(10) NOT NULL,
    movie_id NUMBER(10) NOT NULL,
    theater_id NUMBER(10),
    schedule_date DATE NOT NULL,
    schedule_sequence NUMBER(10) NOT NULL,
    schedule_start_time TIMESTAMP NOT NULL,
    schedule_end_time TIMESTAMP NOT NULL,
    CONSTRAINT pk_schedule PRIMARY KEY (schedule_id),
    CONSTRAINT fk_schedule_movie FOREIGN KEY (movie_id) REFERENCES movie(movie_id),
    CONSTRAINT fk_schedule_theater FOREIGN KEY (theater_id) REFERENCES theater(theater_id)
);

-- 11. 좌석 예약 가능 상태 테이블 (SEAT_AVAILABLE)
CREATE TABLE seat_available (
    schedule_id NUMBER(10) NOT NULL,
    seat_id NUMBER(10) NOT NULL,
    is_booked VARCHAR2(1) NOT NULL,
    CONSTRAINT pk_seat_available PRIMARY KEY (schedule_id, seat_id),
    CONSTRAINT fk_seat_available_schedule FOREIGN KEY (schedule_id) REFERENCES schedule(schedule_id),
    CONSTRAINT fk_seat_available_seat FOREIGN KEY (seat_id) REFERENCES seat(seat_id)
);

-- 12. 쿠폰 테이블 (COUPON)
CREATE TABLE coupon (
    coupon_id NUMBER(10) NOT NULL,
    coupon_name VARCHAR2(50) NOT NULL,
    coupon_description VARCHAR2(200) NOT NULL,
    start_date DATE,
    end_date DATE,
    discount_amount NUMBER(10) DEFAULT 2000,
    CONSTRAINT pk_coupon PRIMARY KEY (coupon_id)
);

-- 13. 발급된 쿠폰 테이블 (ISSUE_COUPON)
CREATE TABLE issue_coupon (
    issue_id NUMBER(10) NOT NULL,
    customer_id NUMBER(10) NOT NULL,
    coupon_id NUMBER(10) NOT NULL,
    issued_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    is_used NUMBER(1) DEFAULT 0 NOT NULL,
    used_at TIMESTAMP,
    CONSTRAINT pk_issue_coupon PRIMARY KEY (issue_id),
    CONSTRAINT fk_issue_coupon_customer FOREIGN KEY (customer_id) REFERENCES customer(customer_id),
    CONSTRAINT fk_issue_coupon_coupon FOREIGN KEY (coupon_id) REFERENCES coupon(coupon_id),
    CONSTRAINT chk_is_used CHECK (is_used IN (0, 1))
);

-- 14. 결제 테이블 (PAYMENT)
CREATE TABLE payment (
    payment_id NUMBER(10) NOT NULL,
    customer_id NUMBER(10) NOT NULL,
    payment_amount NUMBER(10) NOT NULL,
    payment_method VARCHAR2(20) NOT NULL,
    approval_number NUMBER(10),
    payment_status VARCHAR2(20),
    payment_date DATE,
    discount_amount NUMBER(10),
    used_points NUMBER(10),
    payment_key VARCHAR2(255),
    CONSTRAINT pk_payment PRIMARY KEY (payment_id),
    CONSTRAINT fk_payment_customer FOREIGN KEY (customer_id) REFERENCES customer(customer_id)
);

-- 15. 티켓 테이블 (TICKET)
CREATE TABLE ticket (
    ticket_id NUMBER(10) NOT NULL,
    customer_id NUMBER(10) NOT NULL,
    schedule_id NUMBER(10) NOT NULL,
    seat_id NUMBER(10) NOT NULL,
    is_issued VARCHAR2(1) NOT NULL,
    booking_datetime TIMESTAMP NOT NULL,
    payment_id NUMBER(10),
    audience_type VARCHAR2(1) NOT NULL,
    CONSTRAINT pk_ticket PRIMARY KEY (ticket_id),
    CONSTRAINT fk_ticket_customer FOREIGN KEY (customer_id) REFERENCES customer(customer_id),
    CONSTRAINT fk_ticket_schedule FOREIGN KEY (schedule_id) REFERENCES schedule(schedule_id),
    CONSTRAINT fk_ticket_seat FOREIGN KEY (seat_id) REFERENCES seat(seat_id),
    CONSTRAINT fk_ticket_payment FOREIGN KEY (payment_id) REFERENCES payment(payment_id)
);

-- 16. 리뷰 테이블 (REVIEW)
CREATE TABLE review (
    review_id NUMBER(10) NOT NULL,
    movie_id NUMBER(10) NOT NULL,
    customer_id NUMBER(10) NOT NULL,
    star_rating NUMBER(3,1),
    content_desc CLOB,
    date_created DATE,
    CONSTRAINT pk_review PRIMARY KEY (review_id),
    CONSTRAINT fk_review_movie FOREIGN KEY (movie_id) REFERENCES movie(movie_id),
    CONSTRAINT fk_review_customer FOREIGN KEY (customer_id) REFERENCES customer(customer_id),
    CONSTRAINT chk_star_rating CHECK (star_rating >= 0 AND star_rating <= 5)
);

-- ==========================================
-- 인덱스 생성 (Indexes) - 오류 무시
-- CREATE INDEX idx_schedule_date_theater ON schedule(schedule_date, theater_id);
-- CREATE INDEX idx_schedule_date_movie ON schedule(schedule_date, movie_id);
-- CREATE INDEX idx_schedule_movie_time ON schedule(movie_id, schedule_start_time);
-- CREATE INDEX idx_movie_name ON movie(movie_name);
-- CREATE INDEX idx_movie_release_date ON movie(release_date DESC);
-- CREATE INDEX IDX_ACTOR_NAME ON actor(actor_name);
-- CREATE INDEX IDX_GENRE_NAME ON genre(genre_name);
-- CREATE INDEX IDX_REVIEW_MOVIE_ID_FK ON review(movie_id);

-- CREATE INDEX IDX_REVIEW_CUSTOMER_ID ON review(customer_id);
-- CREATE INDEX IDX_CUSTOMER_INPUTID ON customer(customer_input_id);
-- CREATE INDEX IDX_CUSTOMER_LEVEL_ID_FK ON customer(level_id);
-- CREATE INDEX IDX_CUSTOMER_PHONE ON customer(phone);
-- CREATE INDEX IDX_ISSUE_CUSTOMER_ID ON issue_coupon(customer_id);

-- CREATE INDEX IDX_SEAT_THEATER ON seat(theater_id);
-- CREATE UNIQUE INDEX IDX_SEAT_THEATER_ROW_COL
--     ON seat(theater_id, row_number, column_number);

-- CREATE INDEX IDX_PAYMENT_CUSTOMER_STATUS ON payment(customer_id, payment_status);

-- CREATE INDEX IDX_TICKET_PAYMENT_ID ON ticket(payment_id);
-- CREATE INDEX IDX_TICKET_PAYMENT_ID ON ticket(payment_id);
