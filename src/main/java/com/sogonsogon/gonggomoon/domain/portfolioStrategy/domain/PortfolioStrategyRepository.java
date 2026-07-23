package com.sogonsogon.gonggomoon.domain.portfolioStrategy.domain;

import com.sogonsogon.gonggomoon.domain.portfolioStrategy.application.result.PortfolioStrategyListQueryItem;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.application.result.PortfolioStrategyDetailQueryResult;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PortfolioStrategyRepository {
    Optional<PortfolioStrategy> findByIdAndUserId(Long id, Long userId);

    Optional<PortfolioStrategy> findByPublicIdAndUserId(UUID publicId, Long userId);

    Optional<PortfolioStrategy> findFirstByUserIdAndPostAnalysisIdAndStatusOrderByCreatedAtDesc(
            Long userId,
            Long postAnalysisId,
            PortfolioStrategyGenerateStatus status
    );

    PortfolioStrategy save(PortfolioStrategy portfolioStrategy);

    void delete(PortfolioStrategy portfolioStrategy);

    List<PortfolioStrategyListQueryItem> findFirstPortfolioStrategyListByUserId(
            Long userId,
            Pageable pageable
    );

    List<PortfolioStrategyListQueryItem> findNextPortfolioStrategyListByUserId(
            Long userId,
            Instant cursorCreatedAt,
            Long cursorId,
            Pageable pageable
    );

    Optional<PortfolioStrategyDetailQueryResult> findPortfolioStrategyDetailByPublicIdAndUserId(UUID publicId, Long userId);

    boolean existsByUserIdAndGeneratedDate(Long userId, LocalDate today);

    int countByUserIdAndGeneratedDate(Long userId, LocalDate generatedDate);

    Optional<PortfolioStrategy> findByPostIdAndUserId(Long postId, Long userId);
}
