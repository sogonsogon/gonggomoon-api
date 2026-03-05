package com.sogonsogon.gonggomoon.domain.auth.infrastructure;

import com.sogonsogon.gonggomoon.domain.auth.domain.RefreshToken;
import com.sogonsogon.gonggomoon.domain.auth.domain.RefreshTokenRepository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshToken, Long>, RefreshTokenRepository {

    Optional<RefreshToken> findByUserId(Long userId);
}
