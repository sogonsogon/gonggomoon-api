package com.sogonsogon.gonggomoon.domain.auth.infrastructure;

import com.sogonsogon.gonggomoon.domain.auth.domain.RefreshToken;
import com.sogonsogon.gonggomoon.domain.auth.domain.RefreshTokenRepository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshToken, Long>, RefreshTokenRepository {

    Optional<RefreshToken> findByUserId(Long userId);

    @Modifying
    @Query(value = """
        INSERT INTO refresh_tokens (user_id, token)
        VALUES (:userId, :token)
        ON CONFLICT (user_id)
        DO UPDATE SET
            token = EXCLUDED.token
        """, nativeQuery = true)
    void upsertByUserId(@Param("userId") Long userId, @Param("token") String token);
}
