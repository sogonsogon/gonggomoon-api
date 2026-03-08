package com.sogonsogon.gonggomoon.domain.auth.domain;

import java.util.Optional;

public interface OAuthAccountRepository {
    OAuthAccount save(OAuthAccount oAuthAccount);

    Optional<OAuthAccount> findByUserId(Long id);

    void deleteByUserId(Long userId);
}
