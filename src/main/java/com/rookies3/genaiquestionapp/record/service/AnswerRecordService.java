package com.rookies3.genaiquestionapp.record.service;

import com.rookies3.genaiquestionapp.record.controller.dto.AnswerRecordDto;
import com.rookies3.genaiquestionapp.record.entity.AnswerRecord;
import com.rookies3.genaiquestionapp.problem.entity.Problem;
import com.rookies3.genaiquestionapp.auth.entity.User;
import com.rookies3.genaiquestionapp.record.repository.AnswerRecordRepository;
import com.rookies3.genaiquestionapp.problem.repository.ProblemRepository;
import com.rookies3.genaiquestionapp.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnswerRecordService {
    private final AnswerRecordRepository answerRecordRepository;
    private final UserRepository userRepository;
    private final ProblemRepository problemRepository;

    public List<AnswerRecordDto.AnswerRecordDetailResponse> getAnswerRecords(Long userId, boolean isCorrect) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with ID: " + userId));

        List<AnswerRecord> records;
        if (isCorrect) {
            // 오답만 조회
            records = answerRecordRepository.findByUserAndIsCorrectFalseOrderByCreatedAtDesc(user);
        } else {
            // 모든 기록 조회
            records = answerRecordRepository.findByUserOrderByCreatedAtDesc(user);
        }

        return records.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private AnswerRecordDto.AnswerRecordDetailResponse convertToDto(AnswerRecord record) {
        return AnswerRecordDto.AnswerRecordDetailResponse.builder()
                .recordId(record.getId())
                .userRecordsId(record.getUserRecordsId())
                .questionId(record.getProblem().getId())
                .level(record.getProblem().getLevel())
                .problemType(record.getProblem().getProblemType())
                .problemTitleParent(record.getProblem().getProblemTitleParent())
                .problemTitleChild(record.getProblem().getProblemTitleChild())
                .problemContent(record.getProblem().getProblemContent())
                .answerNumber(record.getProblem().getAnswerNumber())
                .userAnswer(record.getUserAnswer())
                .explanation(record.getProblem().getExplanation())
                .createdAt(record.getCreatedAt())
                .updatedAt(record.getUpdatedAt())  // 추가
                .isCorrect(record.isCorrect())
                .build();
    }

    // 저장
    @Transactional
    public AnswerRecordDto.AnswerRecordDetailResponse saveAnswerRecord(Long userId, AnswerRecordDto.AnswerRecordSaveRequest requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다. (ID: " + userId + ")"));

        Problem problem = problemRepository.findById(requestDto.getProblemId())
                .orElseThrow(() -> new NoSuchElementException("문제를 찾을 수 없습니다. (ID: " + requestDto.getProblemId() + ")"));

        // 정답 여부 계산
        boolean isCorrect = problem.getAnswerNumber().equals(requestDto.getUserAnswer());

        // 기존 기록 존재 여부 확인
        Optional<AnswerRecord> existingRecordOpt = answerRecordRepository.findByUserAndProblem(user, problem);

        AnswerRecord answerRecord;
        if (existingRecordOpt.isPresent()) {
            // ✅ 기존 기록이 있다면 업데이트
            answerRecord = existingRecordOpt.get();
            answerRecord.setUserAnswer(requestDto.getUserAnswer());
            answerRecord.setCorrect(isCorrect);
        } else {
            // ✅ 새로 저장
            Integer lastRecordsId = answerRecordRepository.findTopByUserOrderByIdDesc(user)
                    .map(AnswerRecord::getUserRecordsId)
                    .orElse(0);
            Integer newUserRecordsId = lastRecordsId + 1;

            answerRecord = AnswerRecord.builder()
                    .user(user)
                    .problem(problem)
                    .userAnswer(requestDto.getUserAnswer())
                    .isCorrect(isCorrect)
                    .userRecordsId(newUserRecordsId)
                    .build();
        }

        AnswerRecord saved = answerRecordRepository.save(answerRecord);
        return convertToDto(saved);
    }


    // 삭제
    @Transactional
    public void deleteAnswerRecord(Long recordId, Long userId) {
        AnswerRecord recordToDelete = answerRecordRepository.findById(recordId)
                .orElseThrow(() -> new NoSuchElementException("Answer record not found with ID: " + recordId));

        if (!recordToDelete.getUser().getId().equals(userId)) {
            throw new SecurityException("User " + userId + " does not have permission to delete record " + recordId);
        }

        answerRecordRepository.delete(recordToDelete);
    }

    // 레벨 분석
    public List<AnswerRecordDto.AnalysisResponse> analyzeUserPerformance(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다. (ID: " + userId + ")"));

        List<AnswerRecord> userRecords = answerRecordRepository.findByUser(user);
        if (userRecords.isEmpty()) {
            return List.of(); // 기록이 없으면 빈 리스트 반환
        }

        List<AnswerRecordDto.AnalysisResponse> allAnalysisResults = new ArrayList<>();

        // 1. 레벨(level)별로 그룹화
        Map<String, List<AnswerRecord>> recordsByLevel = userRecords.stream()
                .collect(Collectors.groupingBy(record -> record.getProblem().getLevel()));

        // 각 레벨에 대해 다시 문제 유형별로 분석
        recordsByLevel.forEach((level, levelRecords) -> {
            Map<String, List<AnswerRecord>> recordsByTypeInLevel = levelRecords.stream()
                    .collect(Collectors.groupingBy(record -> record.getProblem().getProblemType()));

            List<AnswerRecordDto.AnalysisResult> typeResultsForLevel = recordsByTypeInLevel.entrySet().stream()
                    .map(entry -> {
                        String problemType = entry.getKey();
                        List<AnswerRecord> records = entry.getValue();
                        long totalSolved = records.size();
                        long totalIncorrect = records.stream().filter(record -> !record.isCorrect()).count();
                        double incorrectRate = (totalSolved > 0) ? (double) totalIncorrect / totalSolved * 100 : 0.0;
                        return AnswerRecordDto.AnalysisResult.builder()
                                .category(problemType) // 문제 유형을 카테고리로 사용
                                .totalSolved(totalSolved)
                                .totalIncorrect(totalIncorrect)
                                .incorrectRate(incorrectRate)
                                .build();
                    })
                    // 해당 레벨 내에서 오답률 높은 순으로 정렬
                    .sorted(Comparator.comparingDouble(AnswerRecordDto.AnalysisResult::getIncorrectRate).reversed())
                    .collect(Collectors.toList());

            // 레벨별 분석 결과를 추가
            allAnalysisResults.add(AnswerRecordDto.AnalysisResponse.builder()
                    .type("level_problemType_breakdown") // 새로운 분석 유형 이름
                    .category(level) // 분석된 레벨
                    .results(typeResultsForLevel)
                    .build());
        });

        // 레벨별로 정렬 (예: N1, N2, N3 순)
        allAnalysisResults.sort(Comparator.comparing(AnswerRecordDto.AnalysisResponse::getCategory));

        return allAnalysisResults;
    }
}
