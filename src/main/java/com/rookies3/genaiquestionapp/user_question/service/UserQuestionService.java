package com.rookies3.genaiquestionapp.user_question.service;

import com.rookies3.genaiquestionapp.auth.entity.User;
import com.rookies3.genaiquestionapp.auth.repository.UserRepository;
import com.rookies3.genaiquestionapp.problem.entity.Problem;
import com.rookies3.genaiquestionapp.problem.repository.ProblemRepository;
import com.rookies3.genaiquestionapp.user_question.controller.dto.UserQuestionDto;
import com.rookies3.genaiquestionapp.user_question.entity.UserQuestion;
import com.rookies3.genaiquestionapp.user_question.entity.UserQuestionChat;
import com.rookies3.genaiquestionapp.user_question.repository.UserProblemChatRepository;
import com.rookies3.genaiquestionapp.user_question.repository.UserQuestionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserQuestionService {

    private final UserQuestionRepository userQuestionRepository;
    private final UserProblemChatRepository userProblemChatRepository;
    private final UserRepository userRepository;
    private final ProblemRepository problemRepository;
    private final AIService aiService; // AIService 주입

    @Transactional
    public UserQuestionDto.Response processUserChat(Long userId, Long problemId, UserQuestionDto.Request requestDto) {
        if (requestDto.getUserQuestionId() == null) {
            return createNewQuestionThread(userId, problemId, requestDto.getContent());
        } else {
            return addMessageToQuestionThread(requestDto.getUserQuestionId(), userId, requestDto.getContent());
        }
    }

    private UserQuestionDto.Response createNewQuestionThread(Long userId, Long problemId, String initialUserQuery) {
        if (!StringUtils.hasText(initialUserQuery)) {
            throw new IllegalArgumentException("Initial question content cannot be empty.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with ID: " + userId));
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(() -> new NoSuchElementException("Problem not found with ID: " + problemId));

        Hibernate.initialize(problem.getChoices());

        UserQuestion newQuestionThread = UserQuestion.builder()
                .user(user)
                .problem(problem)
                .build();
        userQuestionRepository.save(newQuestionThread);

        UserQuestionChat userChatMessage = UserQuestionChat.builder()
                .userQuestion(newQuestionThread)
                .content(initialUserQuery)
                .isUser(true)
                .messageOrder(1)
                .build();
        newQuestionThread.addChatMessage(userChatMessage);
        userProblemChatRepository.save(userChatMessage);

        // AI 모델에 전달할 대화 컨텍스트 구성 (System 메시지는 Python 챗봇에서 처리하므로 여기서는 User 메시지만)
        List<Map<String, String>> conversationHistory = new ArrayList<>();
        conversationHistory.add(Map.of("role", "user", "content", initialUserQuery));

        // AIService 호출 시 userQuestionId와 현재 질문, 문제, 대화 기록 전달
        String aiResponseContent = aiService.getAIResponse(
                newQuestionThread.getId(),
                problem,
                initialUserQuery,
                conversationHistory
        );

        UserQuestionChat aiChatMessage = UserQuestionChat.builder()
                .userQuestion(newQuestionThread)
                .content(aiResponseContent)
                .isUser(false)
                .messageOrder(2)
                .build();
        newQuestionThread.addChatMessage(aiChatMessage);
        userProblemChatRepository.save(aiChatMessage);

        return UserQuestionDto.Response.fromEntity(newQuestionThread);
    }

    private UserQuestionDto.Response addMessageToQuestionThread(Long questionThreadId, Long userId, String userMessage) {
        if (!StringUtils.hasText(userMessage)) {
            throw new IllegalArgumentException("User message content cannot be empty.");
        }

        UserQuestion questionThread = userQuestionRepository.findByIdAndUserId(questionThreadId, userId)
                .orElseThrow(() -> new NoSuchElementException("Question thread not found or unauthorized."));

        Problem problem = questionThread.getProblem();

        int lastMessageOrder = userProblemChatRepository.findFirstByUserQuestionIdOrderByMessageOrderDesc(questionThread.getId())
                .map(UserQuestionChat::getMessageOrder)
                .orElse(0);

        // AI 모델에 전달할 전체 대화 컨텍스트 구성
        List<Map<String, String>> conversationHistory = new ArrayList<>();
        // 기존 대화 메시지들을 모두 추가 (Python 챗봇의 ChatRequest에 맞게 role 변환)
        for (UserQuestionChat chat : questionThread.getChatMessages()) {
            conversationHistory.add(Map.of("role", chat.getIsUser() ? "user" : "ai", "content", chat.getContent()));
        }
        // AIService 호출 시 userQuestionId와 현재 질문, 문제, 대화 기록 전달
        String aiResponseContent = aiService.getAIResponse(
                questionThread.getId(),
                problem, // <-- 이 부분이 변경됨
                userMessage,
                conversationHistory
        );

        UserQuestionChat userChatMessage = UserQuestionChat.builder()
                .userQuestion(questionThread)
                .content(userMessage)
                .isUser(true)
                .messageOrder(lastMessageOrder + 1)
                .build();
        questionThread.addChatMessage(userChatMessage);
        userProblemChatRepository.save(userChatMessage);

        UserQuestionChat aiChatMessage = UserQuestionChat.builder()
                .userQuestion(questionThread)
                .content(aiResponseContent)
                .isUser(true)
                .messageOrder(lastMessageOrder + 2)
                .build();
        questionThread.addChatMessage(aiChatMessage);
        userProblemChatRepository.save(aiChatMessage);

        return UserQuestionDto.Response.fromEntity(questionThread);
    }

    public List<UserQuestionDto.ListResponse> getQuestionThreadsForProblem(Long userId, Long problemId) {
        List<UserQuestion> questionThreads = userQuestionRepository.findByUserIdAndProblemIdOrderByCreatedAtDesc(userId, problemId);

        return questionThreads.stream().map(thread -> {
            String initialQueryPreview = userProblemChatRepository.findByUserQuestionIdOrderByMessageOrderAsc(thread.getId())
                    .stream()
                    .filter(chat -> chat.getIsUser() && chat.getMessageOrder() == 1)
                    .map(UserQuestionChat::getContent)
                    .findFirst()
                    .orElse("Initial query not found.");

            String lastMessagePreview = userProblemChatRepository.findFirstByUserQuestionIdOrderByMessageOrderDesc(thread.getId())
                    .map(UserQuestionChat::getContent)
                    .orElse("");

            return UserQuestionDto.ListResponse.builder()
                    .userQuestionId(thread.getId())
                    .problemId(thread.getProblem().getId())
                    .initialQueryPreview(initialQueryPreview)
                    .createdAt(thread.getCreatedAt())
                    .lastUpdatedAt(thread.getUpdatedAt())
                    .lastMessagePreview(lastMessagePreview)
                    .build();
        }).collect(Collectors.toList());
    }

    public UserQuestionDto.Response getQuestionThreadDetails(Long questionThreadId, Long userId) {
        UserQuestion questionThread = userQuestionRepository.findByIdAndUserId(questionThreadId, userId)
                .orElseThrow(() -> new NoSuchElementException("Question thread not found or unauthorized."));

        return UserQuestionDto.Response.fromEntity(questionThread);
    }
}