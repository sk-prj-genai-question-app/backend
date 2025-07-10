package com.rookies3.genaiquestionapp.problem.entity;

import com.rookies3.genaiquestionapp.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString
@Table(name = "choices")
public class Choice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id")
    private Problem problem;

    @Column(name = "number", nullable = false)
    private Integer number;

    @Column(name = "content", nullable = false, length = 2047)
    private String content;

    @Column(name = "is_correct", nullable = false)
    private Boolean isCorrect;

}
