package com.sogonsogon.gonggomoon.domain.auth.infrastructure;

import com.sogonsogon.gonggomoon.domain.auth.domain.OAuthAccount;
import com.sogonsogon.gonggomoon.domain.auth.domain.OAuthAccountRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OAuthAccountJpaRepository extends JpaRepository<OAuthAccount, Long>, OAuthAccountRepository {
    void deleteByUserId(Long userId);
}
