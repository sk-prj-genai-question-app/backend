package com.rookies3.genaiquestionapp.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // AUTH 관련 에러입니다
    AUTH_EMAIL_DUPLICATE_ERROR("A001","중복된 이메일입니다.",HttpStatus.BAD_REQUEST),
    AUTH_PASSWORD_NOT_EQUAL_ERROR("A002","일치하지 않는 비밀번호입니다.",HttpStatus.BAD_REQUEST),
    AUTH_INVALID_TOKEN("A005","유효하지 않은 토큰입니다.",HttpStatus.BAD_REQUEST),
    AUTH_UNAUTHORIZED("A006","권한이 없습니다.",HttpStatus.UNAUTHORIZED),
    AUTH_USER_NOT_FOUND("A007", "AUTH_USER_NOT_FOUND", HttpStatus.BAD_REQUEST);

    // user 관련 에러


    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    public String formatMessage(Object... args) {
        return String.format(message, args);
    }
}
