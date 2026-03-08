package com.sogonsogon.gonggomoon.domain.auth.application;

import com.sogonsogon.gonggomoon.domain.auth.application.exception.RefreshTokenInvalidException;
import com.sogonsogon.gonggomoon.domain.auth.domain.RefreshToken;
import com.sogonsogon.gonggomoon.domain.auth.domain.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final RefreshTokenRepository tokenRepository;

    @Transactional
    public void issueRefreshToken(Long userId, String refreshToken) {
        tokenRepository.upsertByUserId(userId, refreshToken);
    }

    // TODO : 낙관적 락이 필요할 수도 ? (동시성 이슈 방지 위해)
    @Transactional
    public void rotateRefreshToken(Long userId, String oldToken, String newToken) {

        RefreshToken token = tokenRepository.findByUserId(userId)
            .orElseThrow(RefreshTokenInvalidException::new);

        // ❗ 여기서 RTR의 핵심
        if (!token.getToken().equals(oldToken)) {
            throw new RefreshTokenInvalidException();
        }

        // 기존 refresh token은 여기서 "소모됨"
        token.rotate(newToken);
    }

    @Transactional
    public void revokeRefreshToken(Long userId, String refreshToken) {

        RefreshToken token = tokenRepository.findByUserId(userId)
            .orElseThrow(RefreshTokenInvalidException::new);

        // 현재 세션의 토큰이 맞는지 확인
        if (!token.getToken().equals(refreshToken)) {
            throw new RefreshTokenInvalidException();
        }

        // 로그아웃 = refresh token 제거
        tokenRepository.delete(token);
    }
}
