package com.sogonsogon.gonggomoon.domain.auth.domain;

import java.util.Optional;

public interface OAuthAccountRepository {
    OAuthAccount save(OAuthAccount oAuthAccount);

    Optional<OAuthAccount> findByUserId(Long id);

    Optional<OAuthAccount> findByUserIdAndProvider(Long userId, OAuthProvider provider);

    Optional<OAuthAccount> findByProviderAndProviderId(OAuthProvider provider, String providerId);

    void deleteByUserId(Long userId);
}
