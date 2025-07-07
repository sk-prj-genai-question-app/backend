package com.rookies3.genaiquestionapp.auth.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

public class AccessTokenDto {

    @Data
    @AllArgsConstructor
    public static class Response{
        private String accessToken;
    }
}
