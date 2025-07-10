package com.rookies3.genaiquestionapp.user_question.repository;

import com.rookies3.genaiquestionapp.user_question.entity.UserQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository // 스프링 빈으로 등록
public interface UserQuestionRepository extends JpaRepository<UserQuestion, Long> {

    List<UserQuestion> findByUserIdAndProblemIdOrderByCreatedAtDesc(Long userId, Long problemId);

    Optional<UserQuestion> findByIdAndUserId(Long id, Long userId);
}