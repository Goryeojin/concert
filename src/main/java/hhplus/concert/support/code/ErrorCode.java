package hhplus.concert.support.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // HTTP Error
    HTTP_MESSAGE_NOT_READABLE(HttpStatus.BAD_REQUEST, "유효하지 않은 타입이거나 요청 값이 누락되었습니다."),
    MISSING_TOKEN(HttpStatus.UNAUTHORIZED, "토큰 값이 존재하지 않습니다."),

    // Business Error
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "토큰 인증에 실패하였습니다."),
    BEFORE_RESERVATION_AT(HttpStatus.BAD_REQUEST, "예약하기에는 이릅니다."),
    AFTER_DEADLINE(HttpStatus.BAD_REQUEST, "예약 가능 시간이 지났습니다."),
    SEAT_UNAVAILABLE(HttpStatus.BAD_REQUEST, "예약 가능한 좌석이 아닙니다."),
    PAYMENT_TIMEOUT(HttpStatus.BAD_REQUEST, "결제 가능한 시간이 지났습니다."),
    PAYMENT_DIFFERENT_USER(HttpStatus.BAD_REQUEST, "결제자 정보가 불일치합니다."),
    PAYMENT_FAILED_AMOUNT(HttpStatus.BAD_REQUEST, "결제 잔액이 부족합니다."),
    ALREADY_PAID(HttpStatus.BAD_REQUEST, "해당 예약건은 이미 결제되었습니다."),

    // Jpa Not Found
    CONCERT_NOT_FOUND(HttpStatus.NOT_FOUND, "콘서트 정보를 찾을 수 없습니다."),
    SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND, "콘서트 일정을 찾을 수 없습니다."),
    SEAT_NOT_FOUND(HttpStatus.NOT_FOUND, "좌석 정보 찾을 수 없습니다."),
    RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "예약 정보 찾을 수 없습니다."),
    TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 토큰입니다."),
    POINT_NOT_FOUND(HttpStatus.NOT_FOUND, "잔액 정보를 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러가 발생하였습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}