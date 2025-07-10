package com.rookies3.genaiquestionapp.ai.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.rookies3.genaiquestionapp.problem.controller.dto.ChoiceDto;
import com.rookies3.genaiquestionapp.problem.entity.Choice;
import com.rookies3.genaiquestionapp.problem.entity.Problem;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class SubmitProblemDto {

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

        @NotNull(message = "userAnswer must not be null")
        @JsonProperty("user_answer")
        private Integer userAnswer;


        // entity 변환
        public Problem toEntity() {
            Problem problem = Problem.builder()
                    .level(this.level)
                    .problemType(this.problemType)
                    .problemTitleParent(this.problemTitleParent)
                    .problemTitleChild(this.problemTitleChild)
                    .problemContent(this.problemContent)
                    .answerNumber(this.answerNumber)
                    .explanation(this.explanation)
                    .build();

            // Choice 리스트도 변환
            if (this.choices != null) {
                for (ChoiceDto.ChoiceSaveRequest choiceDto : this.choices) {
                    boolean isCorrect = this.answerNumber.equals(choiceDto.getNumber());
                    Choice choice = Choice.builder()
                            .content(choiceDto.getContent())
                            .number(choiceDto.getNumber())
                            .isCorrect(isCorrect) // 정답인 choice만 true (record isCorrect랑 다른 의미)
                            .build();
                    problem.addChoice(choice); // 양방향 연관관계 설정 포함
                }
            }
            return problem;
        }
    }
}
