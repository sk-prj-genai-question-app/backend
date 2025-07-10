package com.rookies3.genaiquestionapp.userquestion.entity;

import com.rookies3.genaiquestionapp.auth.entity.User;
import com.rookies3.genaiquestionapp.entity.BaseEntity;
import com.rookies3.genaiquestionapp.problem.entity.Problem;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@ToString(exclude = {"user", "problem", "chatMessages"})
@Table(name = "user_questions")
public class UserQuestion extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_question_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answer_records", nullable = false)
    private Problem problem;

    @Builder.Default
    @OneToMany(mappedBy = "userQuestion", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("messageOrder ASC") // 메시지 순서대로 정렬하여 가져옴
    private List<UserQuestionChat> chatMessages = new ArrayList<>();

    public void addChatMessage(UserQuestionChat chatMessage) {
        this.chatMessages.add(chatMessage);
        chatMessage.setUserQuestion(this); // 양방향 관계 설정
    }
}
