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
    AUTH_USER_NOT_FOUND("A007", "해당 사용자를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),

    // ai 관련 에러입니다
    AI_SERVICE_UNAVAILABLE("AI_001", "AI 서비스가 현재 응답하지 않습니다. 잠시 후 다시 시도해주세요.", HttpStatus.SERVICE_UNAVAILABLE),
    AI_REQUEST_FAILED("AI_002", "AI 요청 처리 중 오류가 발생했습니다. 관리자에게 문의하세요.", HttpStatus.INTERNAL_SERVER_ERROR),

    // 문제 관련 에러입니다
    PROBLEM_SAVE_FAILED("PROB_001", "문제 저장에 실패했습니다. 입력값을 확인하거나 관리자에게 문의하세요.", HttpStatus.INTERNAL_SERVER_ERROR),
    ANSWER_RECORD_SAVE_FAILED("REC_001", "답변 기록 저장에 실패했습니다. 다시 시도해주세요.", HttpStatus.INTERNAL_SERVER_ERROR),

    UNEXPECTED_ERROR("SYS_001", "예상치 못한 시스템 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    public String formatMessage(Object... args) {
        return String.format(message, args);
    }
}
