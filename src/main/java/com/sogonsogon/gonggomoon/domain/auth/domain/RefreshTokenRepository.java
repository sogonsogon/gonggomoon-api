package com.sogonsogon.gonggomoon.domain.auth.domain;

import java.util.Optional;

public interface RefreshTokenRepository {
    Optional<RefreshToken> findByUserId(Long userId);

    void delete(RefreshToken token);
}
