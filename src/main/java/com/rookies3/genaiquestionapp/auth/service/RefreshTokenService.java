package com.rookies3.genaiquestionapp.auth.service;

import com.rookies3.genaiquestionapp.auth.entity.RefreshToken;
import com.rookies3.genaiquestionapp.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    // refreshToken 갱신 or 업데이트
    public void saveOrUpdate(Long id, String refreshToken, long expiresInDays){
        RefreshToken token = refreshTokenRepository.findById(id)
                .orElse(new RefreshToken());

        token.setId(id);
        token.setRefreshToken(refreshToken);
        token.setCreatedAt(LocalDateTime.now());
        token.setExpiresAt(LocalDateTime.now().plusDays(expiresInDays));

        refreshTokenRepository.save(token);
    }

}
