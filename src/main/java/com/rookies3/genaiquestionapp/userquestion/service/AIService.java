package com.rookies3.genaiquestionapp.userquestion.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rookies3.genaiquestionapp.problem.entity.Problem;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// 이 서비스는 이제 외부 Python FastAPI 챗봇 API를 호출합니다.
@Service
@RequiredArgsConstructor
public class AIService {

    @Value("${python.ai-service.url}")
    private String pythonAiServiceBaseUrl;

    private final RestTemplate restTemplate; // HTTP 요청을 위한 RestTemplate 주입
    private final ObjectMapper objectMapper; // JSON 파싱을 위한 ObjectMapper 주입

    public String getAIResponse(Long userQuestionId, Problem problem, String currentQuestion, List<Map<String, String>> chatHistory) {
        // Python FastAPI 챗봇 API의 완전한 URL
        String chatbotApiUrl = pythonAiServiceBaseUrl + "/user_question_chatbot/ask"; // 변경된 엔드포인트 경로

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Python FastAPI의 ChatRequest 모델에 맞는 요청 바디 생성
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("user_question_id", userQuestionId); // UserQuestion ID 전달
        requestBody.put("question", currentQuestion); // 사용자의 현재 질문
        requestBody.put("chat_history", chatHistory); // 이전 대화 기록
        requestBody.put("problem_id", problem.getId());
        requestBody.put("problem_level", problem.getLevel()); // Problem 엔티티에 getLevel() 메서드 필요
        requestBody.put("problem_type", problem.getProblemType()); // Problem 엔티티에 getProblemType() 메서드 필요
        requestBody.put("problem_title_parent", problem.getProblemTitleParent());
        requestBody.put("problem_title_child", problem.getProblemTitleChild());
        requestBody.put("problem_content", problem.getProblemContent());
        List<Map<String, Object>> choicesData = problem.getChoices().stream()
                .map(choice -> {
                    Map<String, Object> choiceMap = new HashMap<>();
                    choiceMap.put("id", choice.getId());
                    choiceMap.put("number", choice.getNumber());
                    choiceMap.put("content", choice.getContent());
                    choiceMap.put("is_correct", choice.getIsCorrect());
                    return choiceMap;
                })
                .collect(Collectors.toList());
        requestBody.put("problem_choices", choicesData); // 선택지 리스트 전달
        requestBody.put("problem_answer_number", problem.getAnswerNumber());
        requestBody.put("problem_explanation", problem.getExplanation());

        // 디버깅 확인용 로그 추가
        try {
            String requestBodyJson = objectMapper.writeValueAsString(requestBody);
            System.out.println("Sending to Python Chatbot API: " + requestBodyJson);
        } catch (Exception e) {
            System.err.println("Error converting requestBody to JSON: " + e.getMessage());
        }

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            // Python FastAPI 챗봇 API 호출
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(chatbotApiUrl, requestEntity, String.class);

            if (responseEntity.getStatusCode().is2xxSuccessful() && responseEntity.getBody() != null) {
                // Python FastAPI의 ChatResponse 모델에 따라 응답 파싱
                JsonNode root = objectMapper.readTree(responseEntity.getBody());
                return root.path("response").asText(); // "response" 필드의 텍스트 추출
            }
        } catch (HttpClientErrorException e) {
            // HTTP 클라이언트 에러 (4xx) 처리
            System.err.println("Python Chatbot API Client Error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            throw new RuntimeException("Failed to get response from Python Chatbot API: " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            // 다른 예외 (IOEException 등) 처리
            System.err.println("Error calling Python Chatbot API: " + e.getMessage());
            throw new RuntimeException("Error during Python Chatbot API call", e);
        }

        return "No response content from AI chatbot."; // 응답 내용이 없는 경우 기본값
    }
}