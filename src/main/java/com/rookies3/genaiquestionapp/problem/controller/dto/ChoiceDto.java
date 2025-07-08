package com.rookies3.genaiquestionapp.problem.controller.dto;

import com.rookies3.genaiquestionapp.problem.entity.Choice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime; // LocalDateTime 임포트 추가

public class ChoiceDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ChoiceSaveRequest {
        @JsonProperty("number")
        private Integer number;
        @JsonProperty("content")
        private String content;
        @JsonProperty("is_correct")
        private Boolean isCorrect;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ChoiceResponse {
        private Long id;
        private Integer number;
        private String content;
        private Boolean isCorrect;
        private LocalDateTime createdAt; // 추가
        private LocalDateTime updatedAt; // 추가

        public static ChoiceResponse fromEntity(Choice choice) {
            return ChoiceResponse.builder()
                    .id(choice.getId())
                    .number(choice.getNumber())
                    .content(choice.getContent())
                    .isCorrect(choice.getIsCorrect())
                    .createdAt(choice.getCreatedAt()) // 추가
                    .updatedAt(choice.getUpdatedAt()) // 추가
                    .build();
        }
    }
}