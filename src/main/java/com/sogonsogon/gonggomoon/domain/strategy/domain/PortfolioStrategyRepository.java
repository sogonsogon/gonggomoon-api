package com.sogonsogon.gonggomoon.domain.strategy.domain;

import java.util.List;
import java.util.Optional;

public interface PortfolioStrategyRepository {
    Optional<PortfolioStrategy> findByIdAndUserId(Long id, Long userId);

    PortfolioStrategy save(PortfolioStrategy portfolioStrategy);

    List<PortfolioStrategy> findAllByUserIdOrderByCreatedAtDesc(Long userId);
}
