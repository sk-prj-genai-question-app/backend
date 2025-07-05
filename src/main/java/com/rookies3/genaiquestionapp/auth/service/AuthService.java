package com.rookies3.genaiquestionapp.auth.service;

import com.rookies3.genaiquestionapp.auth.controller.dto.LoginDto;
import com.rookies3.genaiquestionapp.auth.controller.dto.SignupDto;
import com.rookies3.genaiquestionapp.auth.entity.CustomUserDetails;
import com.rookies3.genaiquestionapp.auth.entity.RefreshToken;
import com.rookies3.genaiquestionapp.auth.entity.User;
import com.rookies3.genaiquestionapp.auth.repository.RefreshTokenRepository;
import com.rookies3.genaiquestionapp.auth.repository.UserRepository;
import com.rookies3.genaiquestionapp.exception.BusinessException;
import com.rookies3.genaiquestionapp.exception.ErrorCode;
import com.rookies3.genaiquestionapp.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    // 회원가입
    public SignupDto.Response register(SignupDto.Request request) {

        String email = request.getEmail();
        String password = request.getPassword();

        // 이메일 중복 확인
        if(isEmailDuplicate(email)){
            throw new BusinessException(ErrorCode.AUTH_EMAIL_DUPLICATE_ERROR);
        }

        // 페스워드 확인
        passwordCheck(request);

        String encodedPassword = passwordEncoder.encode(password);
        User user = User.builder()
                .email(email)
                .password(encodedPassword)
                .isAdmin(false)
                .build();

        userRepository.save(user);
        return new SignupDto.Response(user.getId(), user.getEmail());
    }


    // 로그인
    public LoginDto.Response login(LoginDto.Request request) {
        // 로그인 인증 처리 (Spring Security)
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        String accessToken = jwtTokenProvider.generateAccessToken(userDetails);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails);

        // 서비스에서 저장 책임을 분리
        refreshTokenService.saveOrUpdate(userDetails.getId(), refreshToken, 7);

        // 응답도 DTO를 별도로 정의해서 반환
        return new LoginDto.Response(accessToken, refreshToken, "Bearer");
    }

    // util
    private boolean isEmailDuplicate(String email) {
        return userRepository.existsByEmail(email);
    }

    private void passwordCheck(SignupDto.Request request) {
        if (!request.getPassword().equals(request.getPasswordCheck())) {
            throw new BusinessException(ErrorCode.AUTH_PASSWORD_NOT_EQUAL_ERROR);
        }
    }
}