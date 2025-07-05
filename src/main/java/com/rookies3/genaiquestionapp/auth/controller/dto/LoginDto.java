package com.rookies3.genaiquestionapp.auth.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

public class LoginDto {

    @Data
    public static class Request{
        private String email;
        private String password;
    }

    @Getter
    @AllArgsConstructor
    public static class Response {
        private String accessToken;
        private String refreshToken;
        private String tokenType;
    }
}
