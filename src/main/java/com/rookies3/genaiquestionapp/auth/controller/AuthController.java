package com.rookies3.genaiquestionapp.auth.controller;

import com.rookies3.genaiquestionapp.auth.controller.dto.LoginDto;
import com.rookies3.genaiquestionapp.auth.controller.dto.SignupDto;
import com.rookies3.genaiquestionapp.auth.controller.dto.TokenDto;
import com.rookies3.genaiquestionapp.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<LoginDto.Response> signin(@RequestBody LoginDto.Request request) {
        LoginDto.Response response = authService.login(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}