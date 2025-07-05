package com.rookies3.genaiquestionapp.auth.controller;

import com.rookies3.genaiquestionapp.auth.controller.dto.AccessTokenDto;
import com.rookies3.genaiquestionapp.auth.controller.dto.LoginDto;
import com.rookies3.genaiquestionapp.auth.controller.dto.SignupDto;
import com.rookies3.genaiquestionapp.auth.controller.dto.TokenDto;
import com.rookies3.genaiquestionapp.auth.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<SignupDto.Response> signup(@RequestBody SignupDto.Request request) {
        SignupDto.Response response = authService.register(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/signin")
    public ResponseEntity<AccessTokenDto.Response> signin(@RequestBody LoginDto.Request request, HttpServletResponse response) {
        LoginDto.Response tokenResponse = authService.login(request);

        // RefreshToken HttpOnly 쿠키 설정
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

    @PostMapping("/refresh")
    public ResponseEntity<LoginDto.Response> refreshAccessToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        LoginDto.Response response = authService.refreshAccessToken(refreshToken);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}