package com.rookies3.genaiquestionapp.auth.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

public class SignupDto {

    @Data
    public static class Request {

        @NotBlank(message="이메일을 입력해주세요.")
        @Pattern(
                regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
                message = "올바른 이메일 형식을 입력해주세요. 예: user@example.com"
        )
        private String email;

        @NotBlank(message="비밀번호를 입력해주세요.")
        private String password;

        @NotBlank(message="비밀번호 재입력해주세요.")
        private String passwordCheck;
    }

    @Data
    @AllArgsConstructor
    @Getter
    public static class Response {
        private Long id;
        private String email;
    }
}
