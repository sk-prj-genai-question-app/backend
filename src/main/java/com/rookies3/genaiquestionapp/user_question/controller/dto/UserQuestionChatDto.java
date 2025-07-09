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
public class UserQuestionChatDto {
    private Long messageId;
    private String content;
    private Boolean isUser;
    private Integer messageOrder;
    private LocalDateTime createdAt;

    public static UserQuestionChatDto fromEntity(UserQuestionChat chat) {
        return UserQuestionChatDto.builder()
                .messageId(chat.getId())
                .content(chat.getContent())
                .isUser(chat.getIsUser())
                .messageOrder(chat.getMessageOrder())
                .createdAt(chat.getCreatedAt())
                .build();
    }
}