package com.sogonsogon.gonggomoon.domain.strategy.generator;

import com.sogonsogon.gonggomoon.domain.experience.domain.Experience;

import java.util.List;

public interface PortfolioStrategyContentGenerator {
    public void request(Long userId, Long portfolioStrategyId, List<Experience> experiences, String positionType, String industryType);
}
