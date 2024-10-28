package hhplus.concert.support.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@Slf4j
//@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * [Exception] 비즈니스 로직 수행이 불가능한 경우
     */
    @ExceptionHandler(CoreException.class)
    public ResponseEntity<ErrorResponse> handleCoreException(CoreException e) {
        switch (e.getErrorType().getLogLevel()) {
            case ERROR -> log.error("Business ERROR Occurred: {}, {}", e.getMessage(), e.getPayload(), e);
            case WARN -> log.warn("Business WARN Occurred: {}, {}", e.getMessage(), e.getPayload(), e);
            default -> log.info("Business INFO Occurred: {}, {}", e.getMessage(), e.getPayload(), e);
        }

        HttpStatus status;
        switch (e.getErrorType().getCode()) {
            case DB_ERROR -> status = HttpStatus.INTERNAL_SERVER_ERROR;
            case CLIENT_ERROR -> status = HttpStatus.BAD_REQUEST;
            case TOKEN_ERROR -> status = HttpStatus.UNAUTHORIZED;
            default -> status = HttpStatus.OK;
        }
        return new ResponseEntity<>(ErrorResponse.of(e), status);
    }

    /**
     * [Exception] API 호출 시 '객체' 혹은 '파라미터' 데이터 값이 유효하지 않은 경우
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        log.error("Method Argument Not Valid: {}, message: {}", ex.getMessage(), errors);
        return new ResponseEntity<>(ErrorResponse.of(ErrorType.CLIENT_ERROR, errors), HttpStatus.BAD_REQUEST);
    }

    /**
     * [Exception] 클라이언트에서 Body로 '객체' 데이터가 넘어오지 않았을 경우
     */
//    @ExceptionHandler(HttpMessageNotReadableException.class)
//    protected ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
//            HttpMessageNotReadableException ex) {
//        log.info(ex.getHttpInputMessage().toString());
//        final ErrorResponse response = ErrorResponse.of(ErrorCode.REQUEST_BODY_MISSING_ERROR, ex.getMessage());
//        log.error("HTTP Message Not Readable: {}", ex.getMessage(), ex);
//        return new ResponseEntity<>(ErrorResponse.of(ErrorType.CLIENT_ERROR), HttpStatus.BAD_REQUEST);
//    }
//
//    /**
//     * [Exception] 클라이언트에서 request로 '파라미터로' 데이터가 넘어오지 않았을 경우
//     */
//    @ExceptionHandler(MissingServletRequestParameterException.class)
//    protected ResponseEntity<ErrorResponse> handleMissingRequestHeaderExceptionException(
//            MissingServletRequestParameterException ex) {
//        log.info(ex.getParameterName());
//        log.info(String.valueOf(ex.getMethodParameter()));
//        log.info(ex.getMessage());
//        log.info(ex.getParameterType());
//        final ErrorResponse response = ErrorResponse.of(ErrorCode.MISSING_REQUEST_PARAMETER_ERROR, ex.getMessage());
//        log.error("handleMissingServletRequestParameterException", ex);
//        return new ResponseEntity<>(ErrorResponse.of(ErrorType.CLIENT_ERROR, errors), HttpStatus.BAD_REQUEST);
//    }

    /**
     * [Exception] 모든 Exception 경우 발생
     */
    @ExceptionHandler(Exception.class)
    protected final ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex) {
        log.error("Server Error Occurred: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(ErrorResponse.of(ErrorType.INTERNAL_ERROR, ex.getMessage()), HttpStatus.OK);
    }
}
