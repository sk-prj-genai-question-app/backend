package com.rookies3.genaiquestionapp.ai.service;

import com.rookies3.genaiquestionapp.ai.controller.dto.AiDto;
import com.rookies3.genaiquestionapp.ai.controller.dto.SubmitProblemDto;
import com.rookies3.genaiquestionapp.auth.entity.User;
import com.rookies3.genaiquestionapp.auth.repository.UserRepository;
import com.rookies3.genaiquestionapp.exception.BusinessException;
import com.rookies3.genaiquestionapp.exception.ErrorCode;
import com.rookies3.genaiquestionapp.problem.controller.dto.ChoiceDto;
import com.rookies3.genaiquestionapp.problem.entity.Choice;
import com.rookies3.genaiquestionapp.problem.entity.Problem;
import com.rookies3.genaiquestionapp.problem.repository.ProblemRepository;
import com.rookies3.genaiquestionapp.record.entity.AnswerRecord;
import com.rookies3.genaiquestionapp.record.repository.AnswerRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.*;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiService {

    private final RestTemplate restTemplate;

    private final ProblemRepository problemRepository;
    private final AnswerRecordRepository answerRecordRepository;
    private final UserRepository userRepository;


    // back - front - AI - 응답
    public Map<String, Object> askAi(Long userId, String question) {
        AiDto.Request aiRequest = new AiDto.Request(question, userId.toString());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AiDto.Request> entity = new HttpEntity<>(aiRequest, headers);

        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    "http://ai-service:8000/chatbot",
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );

            // 200 아닌 다른 HTTP 상태 코드를 반환
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new BusinessException(ErrorCode.AI_REQUEST_FAILED);
            }
            if (response.getBody() == null) {
                throw new BusinessException(ErrorCode.AI_REQUEST_FAILED);
            }
            return response.getBody();

        } catch (ResourceAccessException e) {
            // 네트워크 연결 실패, 타임아웃 등 (AI 서비스가 꺼져있거나 네트워크 문제)
            throw new BusinessException(ErrorCode.AI_SERVICE_UNAVAILABLE);
        } catch (RestClientException e) {
            throw new BusinessException(ErrorCode.AI_REQUEST_FAILED);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.UNEXPECTED_ERROR);
        }
    }

    public void saveProblemAndAnswer(SubmitProblemDto.ProblemSaveRequest dto, Long userId) {

        // user 유효성 검사
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_USER_NOT_FOUND));

        // Problem 생성 및 Choice 매핑
        Problem problem = dto.toEntity();

        try {
            // Problem + Choice 저장
            problemRepository.save(problem);
        } catch (DataIntegrityViolationException e) {
            // 데이터 무결성 제약 조건 위반
            throw new BusinessException(ErrorCode.PROBLEM_SAVE_FAILED);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.PROBLEM_SAVE_FAILED);
        }

        // AnswerRecord 저장
        AnswerRecord answerRecord = createAnswerRecord(dto, user, problem);
        try {
            answerRecordRepository.save(answerRecord);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.ANSWER_RECORD_SAVE_FAILED);
        }
    }

    private AnswerRecord createAnswerRecord(SubmitProblemDto.ProblemSaveRequest dto, User user, Problem problem) {
        boolean isCorrect = dto.getUserAnswer().equals(dto.getAnswerNumber());

        int nextRecordId = answerRecordRepository.findTopByUserOrderByIdDesc(user)
                .map(AnswerRecord::getUserRecordsId)
                .orElse(0) + 1;

        return AnswerRecord.builder()
                .user(user)
                .problem(problem)
                .userAnswer(dto.getUserAnswer())
                .isCorrect(isCorrect)
                .userRecordsId(nextRecordId)
                .build();
    }
}