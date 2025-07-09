package com.rookies3.genaiquestionapp.user_question.entity;

import com.rookies3.genaiquestionapp.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@ToString(exclude = "userQuestion") // 순환 참조 방지
@Table(name = "user_question_chats")
public class UserQuestionChat extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long id;

    // 양방향 관계 설정을 위한 Setter (UserQuestion에서 호출)
    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_question_id", nullable = false)
    private UserQuestion userQuestion; // FK to user_questions table

    @Lob // TEXT 타입 매핑을 위해 사용 (VARCHAR 길이 초과 시)
    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "message_order", nullable = false)
    private Integer messageOrder;

    @Column(name = "is_user", nullable = false) // 'USER'이면 true, 'AI'이면 false
    private Boolean isUser;


}