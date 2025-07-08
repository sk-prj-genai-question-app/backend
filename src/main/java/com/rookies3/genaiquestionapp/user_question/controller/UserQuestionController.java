package com.rookies3.genaiquestionapp.user_question.controller;

import com.rookies3.genaiquestionapp.user_question.controller.dto.UserQuestionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserQuestionController {

    private final UserQuestionService userQuestionService;

    private Long getCurrentUserId() {
        return 1L; // 임시 사용자 ID
    }

    @PostMapping("/problems/{problemId}/chat")
    public ResponseEntity<UserQuestionDto.Response> handleChatRequest(
            @PathVariable Long problemId,
            @Valid @RequestBody UserQuestionDto.Request requestDto) {
        try {
            Long userId = getCurrentUserId();
            UserQuestionDto.Response response = userQuestionService.processUserChat(userId, problemId, requestDto);
            HttpStatus status = (requestDto.getQuestionThreadId() == null) ? HttpStatus.CREATED : HttpStatus.OK;
            return new ResponseEntity<>(response, status);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/problems/{problemId}/questions")
    public ResponseEntity<List<UserQuestionDto.ListResponse>> getQuestionThreadsForProblem(
            @PathVariable Long problemId) {
        try {
            Long userId = getCurrentUserId();
            List<UserQuestionDto.ListResponse> response = userQuestionService.getQuestionThreadsForProblem(userId, problemId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/questions/{questionThreadId}")
    public ResponseEntity<UserQuestionDto.Response> getQuestionThreadDetails(
            @PathVariable Long questionThreadId) {
        try {
            Long userId = getCurrentUserId();
            UserQuestionDto.Response response = userQuestionService.getQuestionThreadDetails(questionThreadId, userId);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}