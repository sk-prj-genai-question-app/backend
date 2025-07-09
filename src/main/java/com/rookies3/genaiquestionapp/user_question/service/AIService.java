package com.rookies3.genaiquestionapp.user_question.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

// 이 서비스는 이제 외부 Python FastAPI 챗봇 API를 호출합니다.
@Service
@RequiredArgsConstructor
public class AIService {

    @Value("${python.ai-service.url}")
    private String pythonAiServiceBaseUrl;

    private final RestTemplate restTemplate; // HTTP 요청을 위한 RestTemplate 주입
    private final ObjectMapper objectMapper; // JSON 파싱을 위한 ObjectMapper 주입

    public String getAIResponse(Long userQuestionId, String currentQuestion, List<Map<String, String>> chatHistory) {
        // Python FastAPI 챗봇 API의 완전한 URL
        String chatbotApiUrl = pythonAiServiceBaseUrl + "/question_chatbot/ask"; // 변경된 엔드포인트 경로

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Python FastAPI의 ChatRequest 모델에 맞는 요청 바디 생성
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("user_question_id", userQuestionId); // UserQuestion ID 전달
        requestBody.put("question", currentQuestion); // 사용자의 현재 질문
        requestBody.put("chat_history", chatHistory); // 이전 대화 기록

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