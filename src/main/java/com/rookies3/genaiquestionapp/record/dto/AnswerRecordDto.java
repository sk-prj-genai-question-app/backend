package com.rookies3.genaiquestionapp.record.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class AnswerRecordDto {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AnswerRecordSaveRequest {
        @JsonProperty("user_id")
        @NonNull
        private Long userId;

        @JsonProperty("problem_id")
        @NonNull
        private Long problemId;

        @JsonProperty("user_answer")
        @NonNull
        private Integer userAnswer;
        // isCorrect는 서버에서 계산하므로 요청 DTO에 포함하지 않습니다.
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AnswerRecordDetailResponse {
        private Long recordId;
        private Integer userRecordsId; // 사용자별 문제풀이 순번
        private Long questionId;
        private String level;
        private String problemType;
        private String problemTitleParent;
        private String problemTitleChild;
        private String problemContent; // 본문
        private Integer answerNumber; // 문제 정답
        private Integer userAnswer; // 사용자 정답
        private String explanation; // 해설
        private boolean isCorrect;
        private LocalDateTime createdAt;
    }

    // 취약 유형 분석
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AnalysisResponse {
        private String type;
        private String category; // (ex. "N1", "N2")
        private List<AnalysisResult> results; // 각 항목별 분석 결과
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AnalysisResult {
        private String category; // 항목 이름 (ex. "문법", "독해")
        private long totalSolved; // 총 푼 문제 수 (해당 카테고리)
        private long totalIncorrect; // 총 틀린 문제 수 (해당 카테고리)
        private double incorrectRate; // 오답률 (틀린 문제 수 / 총 푼 문제 수 * 100)
    }
}