package com.rookies3.genaiquestionapp.problem.service;

import com.rookies3.genaiquestionapp.exception.BusinessException;
import com.rookies3.genaiquestionapp.exception.ErrorCode;
import com.rookies3.genaiquestionapp.problem.controller.dto.ProblemDto;
import com.rookies3.genaiquestionapp.record.controller.dto.AnswerRecordDto;
import com.rookies3.genaiquestionapp.problem.entity.Choice;
import com.rookies3.genaiquestionapp.problem.entity.Problem;
import com.rookies3.genaiquestionapp.problem.repository.ProblemRepository;
import com.rookies3.genaiquestionapp.record.service.AnswerRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList; // ArrayList 임포트 추가
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProblemService {

    private final ProblemRepository problemRepository;
    private final AnswerRecordService answerRecordService;
    private final RestTemplate restTemplate;

    @Value("${ai.problem.generator.url}")
    private String aiProblemGeneratorUrl;

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
                .orElseThrow(() -> new BusinessException(ErrorCode.PROBLEM_NOT_FOUND));
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

    // 취약점 기반 문제 생성 부분
    @Transactional
    public ProblemDto.ProblemDetailResponse generateAndSaveProblemForUserWeakness(Long userId) {
        List<AnswerRecordDto.AnalysisResponse> analysisResults = answerRecordService.analyzeUserPerformance(userId);

        if (analysisResults.isEmpty()) {
            throw new BusinessException(ErrorCode.USER_RECORD_NOT_FOUND);
        }

        String targetLevel = null;
        String targetProblemType = null;
        double maxIncorrectRate = -1.0;

        for (AnswerRecordDto.AnalysisResponse ar : analysisResults) {
            if ("level_problemType_breakdown".equals(ar.getType())) {
                String currentLevel = ar.getCategory();
                for (AnswerRecordDto.AnalysisResult result : ar.getResults()) {
                    if (result.getIncorrectRate() > maxIncorrectRate) {
                        maxIncorrectRate = result.getIncorrectRate();
                        targetLevel = currentLevel;
                        targetProblemType = result.getCategory();
                    }
                }
            }
        }

        if (targetLevel == null || targetProblemType == null || maxIncorrectRate == 0.0) {
            // Logger 사용 권장
            // log.warn("취약점 분석 실패 또는 모든 문제 정답. 기본 문제 유형으로 생성: N1 - 문법");
            targetLevel = "N1";
            targetProblemType = "문법";
        }

        ProblemDto.ProblemSaveRequest aiGeneratedProblemData;
        try {
            aiGeneratedProblemData = callPythonAiProblemGenerator(targetLevel, targetProblemType);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.AI_SERVER_COMMUNICATION_ERROR, e.getMessage());
        }

        Problem problemToSave = Problem.builder()
                .level(aiGeneratedProblemData.getLevel())
                .problemType(aiGeneratedProblemData.getProblemType())
                .problemTitleParent(aiGeneratedProblemData.getProblemTitleParent())
                .problemTitleChild(aiGeneratedProblemData.getProblemTitleChild())
                .problemContent(aiGeneratedProblemData.getProblemContent())
                .answerNumber(aiGeneratedProblemData.getAnswerNumber())
                .explanation(aiGeneratedProblemData.getExplanation())
                .choices(new ArrayList<>()) // choices 리스트는 Problem 엔티티에서 @Builder.Default로 초기화됨
                .build();

        if (aiGeneratedProblemData.getChoices() != null) {
            aiGeneratedProblemData.getChoices().forEach(choiceDto -> {
                Choice choice = Choice.builder()
                        .number(choiceDto.getNumber())
                        .content(choiceDto.getContent())
                        .isCorrect(choiceDto.getIsCorrect())
                        .build();
                problemToSave.addChoice(choice); // addChoice 사용
            });
        }

        Problem savedProblem = problemRepository.save(problemToSave);
        return ProblemDto.ProblemDetailResponse.fromEntity(savedProblem);
    }

    private ProblemDto.ProblemSaveRequest callPythonAiProblemGenerator(String level, String problemType) {
        String pythonProblemType;
        String normalizedProblemType = problemType.trim();
        pythonProblemType = switch (normalizedProblemType) {
            case "V", "G", "R" -> normalizedProblemType; // DB 값이 Python이 기대하는 값과 동일
            default -> {
                System.err.println("Unsupported problem type received from analysis (DB value): '" + problemType + "' (normalized: '" + normalizedProblemType + "')");
                throw new BusinessException(ErrorCode.INVALID_PROBLEM_TYPE, problemType);
            }
        };
        ProblemDto.WeaknessBasedProblemGenerateRequest aiRequest = ProblemDto.WeaknessBasedProblemGenerateRequest.builder()
                .level(level)
                .problemType(pythonProblemType)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ProblemDto.WeaknessBasedProblemGenerateRequest> requestEntity = new HttpEntity<>(aiRequest, headers);

        try {
            ResponseEntity<ProblemDto.ProblemSaveRequest> responseEntity = restTemplate.postForEntity(
                    aiProblemGeneratorUrl,
                    requestEntity,
                    ProblemDto.ProblemSaveRequest.class
            );

            if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
                return responseEntity.getBody();
            } else {
                String errorMessage = "HTTP Status " + responseEntity.getStatusCode() + ", Response Body: " + responseEntity.getBody();
                // Logger 사용 권장
                // log.error(errorMessage);
                throw new BusinessException(ErrorCode.AI_SERVER_COMMUNICATION_ERROR, errorMessage);
            }
        } catch (org.springframework.web.client.RestClientException e) {
            // Logger 사용 권장
            // log.error("HTTP 요청 중 오류 발생: " + e.getMessage(), e);
            throw new BusinessException(ErrorCode.AI_SERVER_COMMUNICATION_ERROR, e.getMessage());
        }
    }
}