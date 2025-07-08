package com.rookies3.genaiquestionapp.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private String message;
    private HttpStatus httpStatus;
    private final ErrorCode errorCode;

    public BusinessException(String message) {
        //417
        this(message, HttpStatus.EXPECTATION_FAILED);
    }

    public BusinessException(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
        this.errorCode = null;
    }

    public BusinessException(ErrorCode errorCode, Object... args) {
        super(errorCode.formatMessage(args));
        this.message = errorCode.formatMessage(args);
        this.httpStatus = errorCode.getHttpStatus();
        this.errorCode = errorCode;

    }
}