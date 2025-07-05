package com.rookies3.genaiquestionapp.auth.service;

import com.rookies3.genaiquestionapp.auth.entity.RefreshToken;
import com.rookies3.genaiquestionapp.auth.repository.RefreshTokenRepository;
import com.rookies3.genaiquestionapp.exception.BusinessException;
import com.rookies3.genaiquestionapp.exception.ErrorCode;
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

    // DB에 저장된 refreshToken 동일성 비교
    public void validateRefreshToken(Long id, String refreshToken) {
        RefreshToken storedToken = refreshTokenRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_INVALID_TOKEN));

        if(!storedToken.getRefreshToken().equals(refreshToken)) {
            throw new BusinessException(ErrorCode.AUTH_INVALID_TOKEN);
        }
    }

    // refreshToken 새로운 토큰으로 갱신
    public void updateRefreshToken(Long id, String newRefreshToken, long expiresInDays) {
        RefreshToken token = refreshTokenRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_INVALID_TOKEN));
        token.setRefreshToken(newRefreshToken);
        token.setExpiresAt(LocalDateTime.now().plusDays(expiresInDays));

        refreshTokenRepository.save(token);
    }
}
