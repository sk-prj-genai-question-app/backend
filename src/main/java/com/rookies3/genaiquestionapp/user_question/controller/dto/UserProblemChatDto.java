package com.rookies3.genaiquestionapp.user_question.controller.dto;

import com.rookies3.genaiquestionapp.user_question.entity.UserQuestionChat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProblemChatDto { // Represents a single chat message
    private Long messageId;
    private String content;
    private String senderType;
    private Integer messageOrder;
    private LocalDateTime createdAt;

    public static UserProblemChatDto fromEntity(UserQuestionChat chat) {
        return UserProblemChatDto.builder()
                .messageId(chat.getId())
                .content(chat.getContent())
                .senderType(chat.getSenderType())
                .messageOrder(chat.getMessageOrder())
                .createdAt(chat.getCreatedAt())
                .build();
    }
}