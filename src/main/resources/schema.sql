CREATE TABLE concert.users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE concert.concert (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    status ENUM('AVAILABLE', 'UNAVAILABLE') NOT NULL,
    INDEX idx_concert_title (title),
    INDEX idx_concert_status (status)
);

CREATE TABLE concert.concert_schedule (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    concert_id BIGINT NOT NULL,
    reservation_at DATETIME NOT NULL,
    deadline DATETIME NOT NULL,
    concert_at DATETIME NOT NULL,
    INDEX idx_concert_id (concert_id),
    INDEX idx_concert_schedule_date (concert_id, reservation_at, deadline)
);

CREATE TABLE concert.seat (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    concert_schedule_id BIGINT NOT NULL,
    seat_no INT NOT NULL,
    status ENUM('AVAILABLE', 'RESERVED') NOT NULL,
    reservation_at DATETIME,
    seat_price INT NOT NULL,
    INDEX idx_concert_schedule_id (concert_schedule_id),
    INDEX idx_concert_schedule_status (concert_schedule_id, status)
);

CREATE TABLE concert.reservation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    concert_id BIGINT NOT NULL,
    concert_schedule_id BIGINT NOT NULL,
    seat_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    status ENUM('PENDING', 'CONFIRMED', 'CANCELLED') NOT NULL,
    reservation_at DATETIME,
    INDEX idx_reservation_user_id (user_id),
    INDEX idx_reservation_status (status),
    INDEX idx_concert_schedule_seat_id (concert_id, concert_schedule_id, seat_id)
);

CREATE TABLE concert.payment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reservation_id BIGINT,
    user_id BIGINT,
    amount INT NOT NULL,
    payment_at DATETIME,
    INDEX idx_payment_user_id (user_id)
);

CREATE TABLE concert.point (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT UNIQUE,
    amount BIGINT NOT NULL,
    last_updated_at DATETIME,
    INDEX idx_point_user_id (user_id)
);
