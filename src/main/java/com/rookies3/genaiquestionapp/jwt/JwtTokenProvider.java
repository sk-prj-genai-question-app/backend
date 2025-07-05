package com.rookies3.genaiquestionapp.jwt;

import com.rookies3.genaiquestionapp.auth.entity.CustomUserDetails;
import com.rookies3.genaiquestionapp.auth.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    private final long ACCESS_TOKEN_VALIDITY = 1000 * 60 * 30; // 30분
    private final long REFRESH_TOKEN_VALIDITY = 1000 * 60 * 60 * 24 * 7; // 7일

    private Key key;

    @PostConstruct
    public void init() {
        // HS256용 키 생성
        key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    // token 발급
    public String generateAccessToken(CustomUserDetails userDetails) {
        Date now = new Date();
        Date accessValidity = new Date(now.getTime() + ACCESS_TOKEN_VALIDITY);

        // payload 설정
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("id", userDetails.getId())
                .claim("roles", userDetails.getAuthorities())
                .setIssuedAt(now)
                .setExpiration(accessValidity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }


    // refresh token 발급
    public String generateRefreshToken(CustomUserDetails userDetails) {
        Date now = new Date();
        Date refreshValidity = new Date(now.getTime() + REFRESH_TOKEN_VALIDITY);

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(now)
                .setExpiration(refreshValidity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰에서 email 추출
    public String getUsernameFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    // 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // util
    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
