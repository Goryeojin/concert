package hhplus.concert.interfaces.exception;

import hhplus.concert.support.code.ErrorCode;
import hhplus.concert.support.exception.CoreException;
import hhplus.concert.support.exception.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CoreException.class)
    protected ResponseEntity<ErrorResponse> handle(CoreException e) {
        log.error("Business Exception Occurred: {}", e.getMessage(), e);
        return ErrorResponse.toResponseEntity(e.getErrorCode());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handle(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        log.error("Method Argument Not Valid: {}, message: {}", ex.getMessage(), errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handle(HttpMessageNotReadableException ex) {
        log.error("Unable to read HTTP message: {}", ex.getMessage());
        return ErrorResponse.toResponseEntity(ErrorCode.HTTP_MESSAGE_NOT_READABLE);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErrorResponse> handle(MissingRequestHeaderException ex) {
        log.error("Missing Request Header: {}", ex.getMessage());
        return ErrorResponse.toResponseEntity(ErrorCode.MISSING_TOKEN);
    }
}
