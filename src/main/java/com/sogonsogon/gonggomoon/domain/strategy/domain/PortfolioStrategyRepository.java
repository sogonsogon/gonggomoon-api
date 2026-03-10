package com.sogonsogon.gonggomoon.domain.strategy.domain;

import java.util.List;

public interface PortfolioStrategyRepository {

    PortfolioStrategy save(PortfolioStrategy portfolioStrategy);

    List<PortfolioStrategy> findAllByUserIdOrderByCreatedAtDesc(Long userId);
}
