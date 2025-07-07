package com.rookies3.genaiquestionapp.problem.entity;

import com.rookies3.genaiquestionapp.problem.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString
@Table(name = "problems")
public class Problem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(name = "level", columnDefinition = "CHAR(2) CHECK (level IN ('N1', 'N2', 'N3'))")
    private String level;

    @Column(name = "problem_type", columnDefinition = "CHAR(1) CHECK (problem_type IN ('V', 'G', 'R'))")
    private String problemType;

    @Column(name = "problem_title_parent", nullable = false)
    private String problemTitleParent;

    @Column(name = "problem_title_child")
    private String problemTitleChild;

    @Column(name = "problem_content", columnDefinition = "TEXT")
    private String problemContent;

    @Column(name = "answer_number", nullable = false, columnDefinition = "INT CHECK (answer_number >= 1 AND answer_number <= 4)")
    private Integer answerNumber;

    @OneToMany(mappedBy = "problem", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Choice> choices = new ArrayList<>();

    @Column(name = "explanation", nullable = false, columnDefinition = "TEXT")
    private String explanation;

    public void addChoice(Choice choice) {
        if (this.choices == null) {
            this.choices = new ArrayList<>(); // 혹시라도 초기화 안 된 경우 방어적 코드
        }
        this.choices.add(choice);
        choice.setProblem(this); // Choice 엔티티에도 Problem 설정 (양방향 관계)
    }
}
