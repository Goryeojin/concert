package hhplus.concert.support.test;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.logging.LogLevel;

@Getter
@AllArgsConstructor
public enum ErrorType {
    INTERNAL_ERROR(ErrorCode.DB_ERROR, "서버 에러가 발생하였습니다.", LogLevel.ERROR),

    RESOURCE_NOT_FOUND(ErrorCode.NOT_FOUND, "리소스를 찾을 수 없습니다.", LogLevel.WARN),

    CLIENT_ERROR(ErrorCode.CLIENT_ERROR, "잘못된 요청입니다.", LogLevel.WARN),
    REQUEST_BODY_MISSING(ErrorCode.CLIENT_ERROR, "잘못된 요청입니다.", LogLevel.WARN),

    BUSINESS_LOGIC_FAILURE(ErrorCode.BUSINESS_ERROR, "비즈니스 로직을 수행할 수 없습니다.", LogLevel.INFO),

    TOKEN_INVALID(ErrorCode.TOKEN_ERROR, "토큰 검증에 실패하였습니다.", LogLevel.WARN);

    private final ErrorCode code;
    private final String message;
    private final LogLevel logLevel;
}
