package com.rookies3.genaiquestionapp.record.controller;

import com.rookies3.genaiquestionapp.auth.repository.UserRepository;
import com.rookies3.genaiquestionapp.problem.repository.ProblemRepository;
import com.rookies3.genaiquestionapp.record.controller.dto.AnswerRecordDto;
import com.rookies3.genaiquestionapp.record.service.AnswerRecordService;
import com.rookies3.genaiquestionapp.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/answer-record")
@RequiredArgsConstructor
public class AnswerRecordController {
    private final AnswerRecordService answerRecordService;
    private final UserRepository userRepository;
    private final ProblemRepository problemRepository;

    @PostMapping
    public ResponseEntity<AnswerRecordDto.AnswerRecordDetailResponse> saveAnswerRecord(Authentication authentication, @Valid @RequestBody AnswerRecordDto.AnswerRecordSaveRequest request) {
        try {
            Long userId = SecurityUtil.extractUserId(authentication);
            AnswerRecordDto.AnswerRecordDetailResponse response = answerRecordService.saveAnswerRecord(userId, request);
            return new ResponseEntity<>(response, HttpStatus.CREATED); // 201 Created
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // 404 Not Found (User 또는 Problem 없음)
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // 500 Internal Server Error
        }
    }
    /**
     * 특정 사용자의 문제 풀이 기록 조회
     * 오답만 조회할 시 ?isCorrect=false 파라미터 사용
     *
     * @param authentication 현재 인증된 사용자 정보
     * @param isCorrect 오답만 조회할지 여부
     * @return 문제풀이 기록 DTO 목록
     */
    @GetMapping("/my-records")
    public ResponseEntity<List<AnswerRecordDto.AnswerRecordDetailResponse>> getAnswerRecordsForUser(
            Authentication authentication,
            @RequestParam(value = "isCorrect", defaultValue = "True") boolean isCorrect) {

        Long userId = SecurityUtil.extractUserId(authentication);
        List<AnswerRecordDto.AnswerRecordDetailResponse> records = answerRecordService.getAnswerRecords(userId, isCorrect);
        if (records.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(records);
    }

    @DeleteMapping("/{recordId}")
    public ResponseEntity<String> deleteAnswerRecordForUser(
            Authentication authentication,
            @PathVariable Long recordId) {

        Long userId = SecurityUtil.extractUserId(authentication);
        try {
            answerRecordService.deleteAnswerRecord(recordId, userId);
            return ResponseEntity.ok("문제 풀이 기록이 성공적으로 삭제되었습니다!"); // 200 OK
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 문제 풀이 기록을 찾을 수 없습니다."); // 404 Not Found
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("기록을 삭제할 권한이 없습니다."); // 403 Forbidden
        } catch (Exception e) {
            // 그 외 예상치 못한 오류 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("기록 삭제 중 오류가 발생했습니다.");
        }
    }

    @GetMapping("/my-analysis")
    public ResponseEntity<List<AnswerRecordDto.AnalysisResponse>> analyzeUserPerformance(Authentication authentication) {
        Long userId = SecurityUtil.extractUserId(authentication);
        try {
            List<AnswerRecordDto.AnalysisResponse> analysisResults = answerRecordService.analyzeUserPerformance(userId);
            if (analysisResults.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(analysisResults);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
