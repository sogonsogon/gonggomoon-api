package com.sogonsogon.gonggomoon.domain.auth.application;

import com.sogonsogon.gonggomoon.domain.auth.application.exception.RefreshTokenInvalidException;
import com.sogonsogon.gonggomoon.domain.auth.infrastructure.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenService tokenService;


    @Transactional
    public void logout(Long userId, String refreshToken) {

        if (refreshToken == null
            || !jwtTokenProvider.validateToken(refreshToken)
            || !jwtTokenProvider.isRefreshToken(refreshToken)) {
            throw new RefreshTokenInvalidException();
        }

        tokenService.revokeRefreshToken(userId, refreshToken);
    }
}
