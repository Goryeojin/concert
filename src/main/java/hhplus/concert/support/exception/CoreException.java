package hhplus.concert.support.exception;

import hhplus.concert.support.code.ErrorType;
import lombok.Getter;

@Getter
public class CoreException extends RuntimeException {
    private final ErrorType errorType;
    private final Object payload;

    public CoreException(ErrorType errorType, Object payload) {
        super(errorType.getMessage());
        this.errorType = errorType;
        this.payload = payload;
    }
}