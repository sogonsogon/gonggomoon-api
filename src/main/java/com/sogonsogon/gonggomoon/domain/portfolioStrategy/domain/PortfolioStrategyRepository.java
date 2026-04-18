package com.sogonsogon.gonggomoon.domain.portfolioStrategy.domain;

import com.sogonsogon.gonggomoon.domain.portfolioStrategy.application.result.PortfolioStrategyListResultItem;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PortfolioStrategyRepository {
    Optional<PortfolioStrategy> findByIdAndUserId(Long id, Long userId);

    PortfolioStrategy save(PortfolioStrategy portfolioStrategy);

    void delete(PortfolioStrategy portfolioStrategy);

    List<PortfolioStrategyListResultItem> findPortfolioStrategyListByUserId(Long userId);

    boolean existsByUserIdAndGeneratedDate(Long userId, LocalDate today);

    int countByUserIdAndGeneratedDate(Long userId, LocalDate generatedDate);
}
