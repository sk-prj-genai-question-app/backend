package com.rookies3.genaiquestionapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // CSRF 보호 비활성화 (API 서버의 경우)
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable())) // H2 Console을 위한 X-Frame-Options 비활성화
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/**", "/h2-console/**").permitAll() // /api/** 및 /h2-console/** 경로의 모든 요청 허용
                        .anyRequest().authenticated() // 그 외 모든 요청은 인증 필요
                );
        return http.build();
    }
}
