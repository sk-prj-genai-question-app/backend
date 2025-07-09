package com.rookies3.genaiquestionapp.ai.controller;

import com.rookies3.genaiquestionapp.ai.controller.dto.AiDto;
import com.rookies3.genaiquestionapp.ai.service.AiService;
import com.rookies3.genaiquestionapp.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Slf4j
public class AiController {

    private final AiService aiService;

    @PostMapping("/ask")
    //public ResponseEntity<?> askAi(Authentication authentication, @RequestBody AiDto.Request request) {
    public ResponseEntity<?> askAi(@RequestBody AiDto.Request request) {
        //Long userId = SecurityUtil.extractUserId(authentication);
        Long userId = 1L;
        Map<String, Object> aiResponse = aiService.askAi(userId, request.getQuestion());
        return ResponseEntity.ok(aiResponse);
    }
}
