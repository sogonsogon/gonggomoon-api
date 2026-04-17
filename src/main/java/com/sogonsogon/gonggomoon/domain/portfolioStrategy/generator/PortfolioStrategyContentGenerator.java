package com.sogonsogon.gonggomoon.domain.portfolioStrategy.generator;

import com.sogonsogon.gonggomoon.domain.experience.domain.Experience;

import java.util.List;

public interface PortfolioStrategyContentGenerator {
    public void request(Long userId, Long portfolioStrategyId, List<Experience> experiences, String positionType, String industryType);
}
