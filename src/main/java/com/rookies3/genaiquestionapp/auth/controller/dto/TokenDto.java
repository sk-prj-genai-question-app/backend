package com.rookies3.genaiquestionapp.auth.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class TokenDto {

    @Getter
    @AllArgsConstructor
    public static class Response {
        private String accessToken;
        private String refreshToken;
    }
}
