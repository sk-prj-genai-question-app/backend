package com.rookies3.genaiquestionapp.config;

import com.rookies3.genaiquestionapp.auth.entity.User;
import com.rookies3.genaiquestionapp.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class AdminAccountInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 관리자 계정이 이미 존재하는지 확인
        if (userRepository.findByEmail("admin@example.com").isEmpty()) {
            // 관리자 계정 생성
            User adminUser = User.builder()
                    .email("admin@example.com")
                    .password(passwordEncoder.encode("admin1234!")) // 안전한 비밀번호로 변경하세요.
                    .isAdmin(true)
                    .build();
            userRepository.save(adminUser);
            System.out.println("관리자 계정 (admin@example.com)이 생성되었습니다.");
        }
    }
}
