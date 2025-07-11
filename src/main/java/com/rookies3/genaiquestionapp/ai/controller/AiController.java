package com.rookies3.genaiquestionapp.ai.controller;

import com.rookies3.genaiquestionapp.ai.controller.dto.AiDto;
import com.rookies3.genaiquestionapp.ai.controller.dto.SubmitProblemDto;
import com.rookies3.genaiquestionapp.ai.service.AiService;
import com.rookies3.genaiquestionapp.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    // 사용자 질문 요청에 대한 응답
    @PostMapping("/ask")
    public ResponseEntity<?> askAi(Authentication authentication, @RequestBody AiDto.Request request) {
        Long userId = SecurityUtil.extractUserId(authentication);
        Map<String, Object> aiResponse = aiService.askAi(userId, request.getQuestion());
        return ResponseEntity.ok(aiResponse);
    }

    // 생성 문제 및 사용자 기록 저장
    @PostMapping("/problem-submit")
    public ResponseEntity<String> submitProblemWithAnswer(Authentication authentication, @RequestBody SubmitProblemDto.ProblemSaveRequest requestDto) {
        Long userId = SecurityUtil.extractUserId(authentication);
        aiService.saveProblemAndAnswer(requestDto, userId);
        return ResponseEntity.ok("문제 및 답안 저장 완료");
    }
}
