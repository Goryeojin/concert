CREATE TABLE users (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255)
);

CREATE TABLE concert (
    id BIGINT PRIMARY KEY,
    title VARCHAR(255),
    description VARCHAR(255),
    status VARCHAR(50)
);

CREATE TABLE concert_schedule (
    id BIGINT PRIMARY KEY,
    concert_id BIGINT, -- 외래 키 설정 안함
    reservation_at TIMESTAMP,
    deadline TIMESTAMP,
    concert_at TIMESTAMP
);

CREATE TABLE seat (
    id BIGINT PRIMARY KEY,
    concert_schedule_id BIGINT, -- 외래 키 설정 안함
    seat_no INT,
    status VARCHAR(50),
    reservation_at TIMESTAMP,
    seat_price INT
);

CREATE TABLE reservation (
    id BIGINT PRIMARY KEY,
    concert_id BIGINT, -- 외래 키 설정 안함
    concert_schedule_id BIGINT, -- 외래 키 설정 안함
    seat_id BIGINT, -- 외래 키 설정 안함
    user_id BIGINT, -- 외래 키 설정 안함
    status VARCHAR(50),
    reservation_at TIMESTAMP
);

CREATE TABLE payment (
    id BIGINT PRIMARY KEY,
    reservation_id BIGINT, -- 외래 키 설정 안함
    user_id BIGINT, -- 외래 키 설정 안함
    amount INT,
    payment_at TIMESTAMP
);

CREATE TABLE point (
    id BIGINT PRIMARY KEY,
    user_id BIGINT, -- 외래 키 설정 안함
    amount INT,
    last_updated_at TIMESTAMP
);
