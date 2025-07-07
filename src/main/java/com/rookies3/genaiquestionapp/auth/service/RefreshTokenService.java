package com.rookies3.genaiquestionapp.auth.service;

import com.rookies3.genaiquestionapp.auth.entity.RefreshToken;
import com.rookies3.genaiquestionapp.auth.repository.RefreshTokenRepository;
import com.rookies3.genaiquestionapp.exception.BusinessException;
import com.rookies3.genaiquestionapp.exception.ErrorCode;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    // refreshToken 갱신 or 업데이트
    @Transactional
    public void saveOrUpdate(Long userId, String refreshToken, long expiresInDays){
        RefreshToken token = refreshTokenRepository.findByUserId(userId)
                .orElse(new RefreshToken());

        token.setUserId(userId);
        token.setRefreshToken(refreshToken);
        token.setCreatedAt(LocalDateTime.now());
        token.setExpiresAt(LocalDateTime.now().plusDays(expiresInDays));

        refreshTokenRepository.save(token);
    }

    // DB에 저장된 refreshToken 동일성 비교
    public void validateRefreshToken(Long userId, String refreshToken) {
        RefreshToken storedToken = refreshTokenRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_INVALID_TOKEN));

        if(!storedToken.getRefreshToken().equals(refreshToken)) {
            throw new BusinessException(ErrorCode.AUTH_INVALID_TOKEN);
        }
    }

    // refreshToken 새로운 토큰으로 갱신
    @Transactional
    public void updateRefreshToken(Long userId, String newRefreshToken, long expiresInDays) {
        RefreshToken token = refreshTokenRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_INVALID_TOKEN));
        token.setRefreshToken(newRefreshToken);
        token.setExpiresAt(LocalDateTime.now().plusDays(expiresInDays));

        refreshTokenRepository.save(token);
    }

    // refreshToken 삭제 (로그아웃)
    @Transactional
    public void deleteRefreshToken(Long userId) {
        refreshTokenRepository.findByUserId(userId)
                .ifPresent(refreshTokenRepository::delete);
    }
}
