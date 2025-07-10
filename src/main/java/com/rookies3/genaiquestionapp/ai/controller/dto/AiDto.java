package com.rookies3.genaiquestionapp.ai.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class AiDto {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request {
        private String question;
        private String userId;  // 서버에서 넣어주는 값
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {
        private String answer;
        private String userId;
    }
}
