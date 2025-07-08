package com.rookies3.genaiquestionapp.problem.controller.dto;

import com.rookies3.genaiquestionapp.problem.entity.Problem;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty; // 이 임포트 추가

import java.time.LocalDateTime; // LocalDateTime 임포트 추가
import java.util.List;
import java.util.stream.Collectors;

public class ProblemDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProblemSaveRequest {
        @NotNull(message = "level must not be null")
        @JsonProperty("level")
        private String level;

        @NotNull(message = "problemType must not be null")
        @JsonProperty("problem_type")
        private String problemType;

        @NotNull(message = "problemTitleParent must not be null")
        @JsonProperty("problem_title_parent")
        private String problemTitleParent;

        @JsonProperty("problem_title_child")
        private String problemTitleChild;

        @JsonProperty("problem_content")
        private String problemContent;

        @NotNull(message = "choices must not be null")
        @JsonProperty("choices")
        private List<ChoiceDto.ChoiceSaveRequest> choices;

        @NotNull(message = "answerNumber must not be null")
        @JsonProperty("answer_number")
        private Integer answerNumber;

        @NotNull(message = "explanation must not be null")
        @JsonProperty("explanation")
        private String explanation;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProblemDetailResponse {
        private Long id;
        private String level;
        private String problemType;
        private String problemTitleParent;
        private String problemTitleChild;
        private String problemContent;
        private List<ChoiceDto.ChoiceResponse> choices;
        private Integer answerNumber;
        private String explanation;
        private LocalDateTime createdAt; // 추가
        private LocalDateTime updatedAt; // 추가

        public static ProblemDetailResponse fromEntity(Problem problem) {
            return ProblemDetailResponse.builder()
                    .id(problem.getId())
                    .level(problem.getLevel())
                    .problemType(problem.getProblemType())
                    .problemTitleParent(problem.getProblemTitleParent())
                    .problemTitleChild(problem.getProblemTitleChild())
                    .problemContent(problem.getProblemContent())
                    .choices(problem.getChoices().stream()
                            .map(ChoiceDto.ChoiceResponse::fromEntity)
                            .collect(Collectors.toList()))
                    .answerNumber(problem.getAnswerNumber())
                    .explanation(problem.getExplanation())
                    .createdAt(problem.getCreatedAt()) // 추가
                    .updatedAt(problem.getUpdatedAt()) // 추가
                    .build();
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProblemListPagination {
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;
        private boolean first;
        private boolean last;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProblemPageResponse {
        private ProblemListPagination pagination;
        private List<ProblemDetailResponse> content;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ApiResponseWrapper<T> {
        private boolean success;
        private T data;
        private Object error;
        private String message;
        private LocalDateTime timestamp = LocalDateTime.now();
        private String requestId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class WeaknessBasedProblemGenerateRequest {
        @JsonProperty("level")
        private String level;
        @JsonProperty("problem_type") // "V", "G", "R" 코드를 사용
        private String problemType;
    }


}