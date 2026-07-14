package com.sogonsogon.gonggomoon.domain.portfolioStrategy.domain;

import com.sogonsogon.gonggomoon.domain.portfolioStrategy.application.result.PortfolioStrategyListResultItem;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.application.result.PortfolioStrategyDetailQueryResult;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PortfolioStrategyRepository {
    Optional<PortfolioStrategy> findByIdAndUserId(Long id, Long userId);

    Optional<PortfolioStrategy> findFirstByUserIdAndPostAnalysisIdAndStatusOrderByCreatedAtDesc(
            Long userId,
            Long postAnalysisId,
            PortfolioStrategyGenerateStatus status
    );

    PortfolioStrategy save(PortfolioStrategy portfolioStrategy);

    void delete(PortfolioStrategy portfolioStrategy);

    List<PortfolioStrategyListResultItem> findPortfolioStrategyListByUserId(Long userId);

    Optional<PortfolioStrategyDetailQueryResult> findPortfolioStrategyDetailByIdAndUserId(Long id, Long userId);

    boolean existsByUserIdAndGeneratedDate(Long userId, LocalDate today);

    int countByUserIdAndGeneratedDate(Long userId, LocalDate generatedDate);

    Optional<PortfolioStrategy> findByPostAnalysisId(Long analysisId);
}
