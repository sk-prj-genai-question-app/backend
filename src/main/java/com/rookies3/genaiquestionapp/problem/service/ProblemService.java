package com.rookies3.genaiquestionapp.problem.service;

import com.rookies3.genaiquestionapp.problem.controller.dto.ProblemDto;
import com.rookies3.genaiquestionapp.problem.entity.Choice;
import com.rookies3.genaiquestionapp.problem.entity.Problem;
import com.rookies3.genaiquestionapp.problem.repository.ProblemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList; // ArrayList 임포트 추가
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProblemService {

    private final ProblemRepository problemRepository;

    @Transactional
    public ProblemDto.ProblemDetailResponse addProblem(ProblemDto.ProblemSaveRequest requestDto) {
        Problem problem = Problem.builder()
                .level(requestDto.getLevel())
                .problemType(requestDto.getProblemType())
                .problemTitleParent(requestDto.getProblemTitleParent())
                .problemTitleChild(requestDto.getProblemTitleChild())
                .problemContent(requestDto.getProblemContent())
                .answerNumber(requestDto.getAnswerNumber())
                .explanation(requestDto.getExplanation())
                .choices(new ArrayList<>()) // 이 줄을 추가하여 choices 리스트를 초기화합니다.
                .build();

        problem.getChoices().addAll(requestDto.getChoices().stream()
                .map(choiceDto -> Choice.builder()
                        .problem(problem)
                        .number(choiceDto.getNumber())
                        .content(choiceDto.getContent())
                        .isCorrect(choiceDto.getIsCorrect())
                        .build())
                .collect(Collectors.toList()));

        Problem savedProblem = problemRepository.save(problem);

        return ProblemDto.ProblemDetailResponse.fromEntity(savedProblem);
    }

    @Transactional(readOnly = true)
    public ProblemDto.ProblemDetailResponse getProblemById(Long id) {
        Problem problem = problemRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Problem not found with id: " + id));
        return ProblemDto.ProblemDetailResponse.fromEntity(problem);
    }

    @Transactional(readOnly = true)
    public ProblemDto.ProblemPageResponse getProblemsByLevel(String level, Pageable pageable) {
        Page<Problem> problemPage = problemRepository.findByLevel(level, pageable);
        return buildProblemPageResponse(problemPage);
    }

    @Transactional(readOnly = true)
    public ProblemDto.ProblemPageResponse getProblemsByProblemType(String problemType, Pageable pageable) {
        Page<Problem> problemPage = problemRepository.findByProblemType(problemType, pageable);
        return buildProblemPageResponse(problemPage);
    }

    @Transactional(readOnly = true)
    public ProblemDto.ProblemPageResponse getAllProblems(Pageable pageable) {
        Page<Problem> problemPage = problemRepository.findAll(pageable);
        return buildProblemPageResponse(problemPage);
    }

    private ProblemDto.ProblemPageResponse buildProblemPageResponse(Page<Problem> problemPage) {
        List<ProblemDto.ProblemDetailResponse> content = problemPage.getContent().stream()
                .map(ProblemDto.ProblemDetailResponse::fromEntity)
                .collect(Collectors.toList());

        ProblemDto.ProblemListPagination pagination = ProblemDto.ProblemListPagination.builder()
                .page(problemPage.getNumber())
                .size(problemPage.getSize())
                .totalElements(problemPage.getTotalElements())
                .totalPages(problemPage.getTotalPages())
                .first(problemPage.isFirst())
                .last(problemPage.isLast())
                .build();

        return ProblemDto.ProblemPageResponse.builder()
                .pagination(pagination)
                .content(content)
                .build();
    }
}