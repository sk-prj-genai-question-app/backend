package com.rookies3.genaiquestionapp.ai.service;

import com.rookies3.genaiquestionapp.ai.controller.dto.AiDto;
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
}