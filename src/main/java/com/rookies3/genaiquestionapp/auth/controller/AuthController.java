package com.rookies3.genaiquestionapp.auth.controller;

import com.rookies3.genaiquestionapp.auth.controller.dto.AccessTokenDto;
import com.rookies3.genaiquestionapp.auth.controller.dto.LoginDto;
import com.rookies3.genaiquestionapp.auth.controller.dto.SignupDto;
import com.rookies3.genaiquestionapp.auth.controller.dto.TokenDto;
import com.rookies3.genaiquestionapp.auth.service.AuthService;
import com.rookies3.genaiquestionapp.exception.BusinessException;
import com.rookies3.genaiquestionapp.exception.ErrorCode;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<SignupDto.Response> signup(@RequestBody SignupDto.Request request) {
        SignupDto.Response response = authService.register(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 로그인
    @PostMapping("/signin")
    public ResponseEntity<AccessTokenDto.Response> signin(@RequestBody LoginDto.Request request, HttpServletResponse response) {
        LoginDto.Response tokenResponse = authService.login(request);

        // RefreshToken HttpOnly 쿠키 설정, 7일
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", tokenResponse.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .build();

        response.setHeader("Set-Cookie", refreshTokenCookie.toString());

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
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", tokenResponse.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .build();
        response.setHeader("Set-Cookie", refreshTokenCookie.toString());

        // AccessToken 응답
        return ResponseEntity.ok(new AccessTokenDto.Response(tokenResponse.getAccessToken()));
    }

    // 확인
    @GetMapping("/me/information")
    public ResponseEntity<String> me(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok().body(userDetails.getUsername());
    }
}