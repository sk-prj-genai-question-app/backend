package com.rookies3.genaiquestionapp.jwt;

import com.rookies3.genaiquestionapp.auth.entity.CustomUserDetails;
import com.rookies3.genaiquestionapp.auth.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 헤더에서 토큰 꺼냄
        String token = resolveToken(request);

        // SecurityContext에 사용자 정보 등록함
        if(token != null && jwtTokenProvider.validateToken(token)){
            String email = jwtTokenProvider.getUsernameFromToken(token);
            CustomUserDetails userDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(email);

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        // 필터로 요청 전달함
        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request){
        String bearer = request.getHeader("Authorization");
        return (bearer != null && bearer.startsWith("Bearer")) ? bearer.substring(7) : null;
    }
}
