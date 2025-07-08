// AnswerRecordRepository.java
package com.rookies3.genaiquestionapp.record.repository;

import com.rookies3.genaiquestionapp.record.entity.AnswerRecord;
import com.rookies3.genaiquestionapp.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface AnswerRecordRepository extends JpaRepository<AnswerRecord, Long> {
    // 특정 사용자의 모든 문제 풀이 기록 조회
    List<AnswerRecord> findByUserOrderByCreatedAtDesc(User user);
    // 특정 사용자의 오답 기록을 최신순으로 조회 (isCorrect가 false인 경우)
    List<AnswerRecord> findByUserAndIsCorrectFalseOrderByCreatedAtDesc(User user);

    // 특정 사용자의 최근 문제 풀이 저장 순번 조회
    Optional<AnswerRecord> findTopByUserOrderByIdDesc(User user);

    List<AnswerRecord> findByUser(User user);
}