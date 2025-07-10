package com.rookies3.genaiquestionapp.ai.service;

import com.rookies3.genaiquestionapp.ai.controller.dto.AiDto;
import com.rookies3.genaiquestionapp.ai.controller.dto.SubmitProblemDto;
import com.rookies3.genaiquestionapp.auth.entity.User;
import com.rookies3.genaiquestionapp.auth.repository.UserRepository;
import com.rookies3.genaiquestionapp.problem.controller.dto.ChoiceDto;
import com.rookies3.genaiquestionapp.problem.entity.Choice;
import com.rookies3.genaiquestionapp.problem.entity.Problem;
import com.rookies3.genaiquestionapp.problem.repository.ProblemRepository;
import com.rookies3.genaiquestionapp.record.entity.AnswerRecord;
import com.rookies3.genaiquestionapp.record.repository.AnswerRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AiService {

    private final RestTemplate restTemplate;

    private final ProblemRepository problemRepository;
    private final AnswerRecordRepository answerRecordRepository;
    private final UserRepository userRepository;


    // back -> front -> ai -> 응답
    public Map<String, Object> askAi(Long userId, String question) {
        AiDto.Request aiRequest = new AiDto.Request(question, userId.toString());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AiDto.Request> entity = new HttpEntity<>(aiRequest, headers);

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                "http://localhost:8000/chatbot",
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        return response.getBody();
    }

    public void saveProblemAndAnswer(SubmitProblemDto.ProblemSaveRequest dto, Long userId) {

        // user 유효성 검사
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Problem 생성 및 Choice 매핑
        Problem problem = dto.toEntity();
        problemRepository.save(problem);

        // Problem + Choice 저장
        problemRepository.save(problem);

        // AnswerRecord 저장
        AnswerRecord answerRecord = createAnswerRecord(dto, user, problem);
        answerRecordRepository.save(answerRecord);
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