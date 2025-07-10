package com.rookies3.genaiquestionapp.auth.controller;

import com.rookies3.genaiquestionapp.auth.controller.dto.AccessTokenDto;
import com.rookies3.genaiquestionapp.auth.controller.dto.LoginDto;
import com.rookies3.genaiquestionapp.auth.controller.dto.SignupDto;
import com.rookies3.genaiquestionapp.auth.entity.CustomUserDetails;
import com.rookies3.genaiquestionapp.auth.service.AuthService;
import com.rookies3.genaiquestionapp.auth.service.RefreshTokenService;
import com.rookies3.genaiquestionapp.exception.BusinessException;
import com.rookies3.genaiquestionapp.exception.ErrorCode;
import com.rookies3.genaiquestionapp.util.SecurityUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<SignupDto.Response> signup(@Valid @RequestBody SignupDto.Request request) {
        SignupDto.Response response = authService.register(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 로그인
    @PostMapping("/signin")
    public ResponseEntity<AccessTokenDto.Response> signin(@RequestBody LoginDto.Request request, HttpServletResponse response) {
        LoginDto.Response tokenResponse = authService.login(request);

        // RefreshToken HttpOnly 쿠키 설정, 7일
        response.setHeader("Set-Cookie", createRefreshTokenCookie(tokenResponse.getRefreshToken()).toString());

        return ResponseEntity.status(HttpStatus.OK).body(new AccessTokenDto.Response(tokenResponse.getAccessToken()));
    }

    // 새로운 토큰 발급
    @PostMapping("/refresh")
    public ResponseEntity<AccessTokenDto.Response> refreshAccessToken(@CookieValue(value = "refreshToken", required = false) String refreshToken,
                                                                      HttpServletResponse response) {
        if(refreshToken == null){
            throw new BusinessException(ErrorCode.AUTH_INVALID_TOKEN);
        }

        // 새로운 토큰값 생성
        LoginDto.Response tokenResponse = authService.refreshAccessToken(refreshToken);

        // 쿠키 재설정, 7일
        response.setHeader("Set-Cookie", createRefreshTokenCookie(tokenResponse.getRefreshToken()).toString());

        // AccessToken 응답
        return ResponseEntity.ok(new AccessTokenDto.Response(tokenResponse.getAccessToken()));
    }

    // 확인
    @GetMapping("/me/information")
    public ResponseEntity<String> me(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok().body(userDetails.getUsername());
    }

    // 로그아웃
    @PostMapping("/signout")
    public ResponseEntity<Void> signOut(HttpServletResponse response) {
        // 로그인된 사용자 인증 정보
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
                authentication.getPrincipal().equals("anonymousUser")) {
            throw new BusinessException(ErrorCode.AUTH_UNAUTHORIZED);
        }
        Long userId = SecurityUtil.extractUserId(authentication);

        // 서버에서 토큰 삭제
        refreshTokenService.deleteRefreshToken(userId);

        // 쿠키 삭제
        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(0)
                .build();
        response.setHeader("Set-Cookie", deleteCookie.toString());

        return ResponseEntity.noContent().build();
    }

    // util
    private ResponseCookie createRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(7 * 24 * 60 * 60)  // 7일
                .build();
    }

    @PostMapping("/verify-password")
    public ResponseEntity<?> verifyPassword(@RequestBody Map<String, String> request, @AuthenticationPrincipal CustomUserDetails userDetails) {
        String password = request.get("password");
        boolean isMatch = authService.verifyPassword(userDetails.getEmail(), password);
        if (isMatch) {
            return ResponseEntity.ok(Map.of("success", true, "message", "비밀번호 일치"));
        } else {
            return ResponseEntity.ok(Map.of("success", false, "message", "비밀번호가 틀렸습니다."));
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> request, @AuthenticationPrincipal CustomUserDetails userDetails) {
        String newPassword = request.get("newPassword");
        authService.changePassword(userDetails.getEmail(), newPassword);
        return ResponseEntity.ok(Map.of("success", true, "message", "비밀번호 변경 성공"));
    }
}