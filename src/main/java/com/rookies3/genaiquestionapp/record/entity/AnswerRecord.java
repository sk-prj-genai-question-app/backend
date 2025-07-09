package com.rookies3.genaiquestionapp.record.entity;

import com.rookies3.genaiquestionapp.entity.BaseEntity;
import com.rookies3.genaiquestionapp.auth.entity.User;
import com.rookies3.genaiquestionapp.problem.entity.Problem;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@ToString(exclude = {"user", "problem"})
@Table(name = "answer_records")
public class AnswerRecord extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_records_id") // 사용자별 문제 풀이 순번
    private Integer userRecordsId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    @Column(name = "user_answer", nullable = false)
    private Integer userAnswer;

    @Column(name = "is_correct",nullable = false)
    private boolean isCorrect;

    public void setUserAnswer(Integer userAnswer) {
        this.userAnswer = userAnswer;
    }

    public void setCorrect(boolean isCorrect) {
        this.isCorrect = isCorrect;
    }

}
