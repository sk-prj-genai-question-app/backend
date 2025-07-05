package com.rookies3.genaiquestionapp.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    USER_NOT_FOUND("U001", "해당 사용자를 찾을 수 없습니다. ID: %s", HttpStatus.NOT_FOUND),
    USER_NOT_FOUND_BY_EMAIL("U002", "해당 사용자를 찾을 수 없습니다. Email: %s", HttpStatus.NOT_FOUND),
    USER_NOT_FOUND_BY_USERNAME("U003", "해당 사용자를 찾을 수 없습니다. 이름: %s", HttpStatus.NOT_FOUND),

    //    AUTH 관련 에러입니다
    AUTH_EMAIL_DUPLICATE_ERROR("A001","중복된 이메일입니다.",HttpStatus.BAD_REQUEST),
    AUTH_PASSWORD_NOT_EQUAL_ERROR("A002","일치하지 않는 비밀번호입니다.",HttpStatus.BAD_REQUEST),
    AUTH_EMAIL_NOT_FOUND("A003","가입되지 않은 이메일입니다.",HttpStatus.NOT_FOUND),
    AUTH_NOT_FOUND_BY_ID("A004","찾을 수 없는 ID입니다.",HttpStatus.NOT_FOUND),
    AUTH_INVALID_TOKEN("A005","유효하지 않은 토큰입니다.",HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    public String formatMessage(Object... args) {
        return String.format(message, args);
    }
}
