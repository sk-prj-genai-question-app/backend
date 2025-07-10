package com.rookies3.genaiquestionapp.userquestion.repository;

import com.rookies3.genaiquestionapp.userquestion.entity.UserQuestionChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository // 스프링 빈으로 등록
public interface UserProblemChatRepository extends JpaRepository<UserQuestionChat, Long> {
    Optional<UserQuestionChat> findFirstByUserQuestionIdOrderByMessageOrderDesc(Long userQuestionId);

    List<UserQuestionChat> findByUserQuestionIdOrderByMessageOrderAsc(Long userQuestionId);
}