package com.rookies3.genaiquestionapp.user_question.controller.dto;

import com.rookies3.genaiquestionapp.user_question.entity.UserQuestion;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class UserQuestionDto {

    // 메시지 추가
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Request {
        // 기존 스레드에 메시지를 추가하는 경우 사용 (null 허용)
        private Long questionThreadId;

        // 사용자 질문 또는 추가 메시지 내용
        @NotBlank(message = "Question or message content cannot be blank")
        private String content;
    }

    // 응답
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long questionThreadId;
        private Long problemId;
        private Long userId;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private List<UserQuestionChatDto> chatMessages; // UserProblemChatDto 참조

        public static Response fromEntity(UserQuestion userQuestion) {
            List<UserQuestionChatDto> messages = userQuestion.getChatMessages().stream()
                    .map(UserQuestionChatDto::fromEntity) // UserProblemChatDto의 fromEntity 호출
                    .collect(Collectors.toList());

            return Response.builder()
                    .questionThreadId(userQuestion.getId())
                    .problemId(userQuestion.getProblem().getId())
                    .userId(userQuestion.getUser().getId())
                    .createdAt(userQuestion.getCreatedAt())
                    .updatedAt(userQuestion.getUpdatedAt())
                    .chatMessages(messages)
                    .build();
        }
    }

    // 목록 조회 - 안 쓰여도 됨
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ListResponse {
        private Long questionThreadId;
        private Long problemId;
        private String initialQueryPreview;
        private LocalDateTime createdAt;
        private LocalDateTime lastUpdatedAt;
        private String lastMessagePreview;
    }
}