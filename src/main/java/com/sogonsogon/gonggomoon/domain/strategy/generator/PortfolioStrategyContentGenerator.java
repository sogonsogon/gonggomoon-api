package com.sogonsogon.gonggomoon.domain.strategy.generator;

import com.sogonsogon.gonggomoon.domain.experience.domain.Experience;
import com.sogonsogon.gonggomoon.domain.strategy.api.request.GeneratePortfolioStrategyRequest;
import com.sogonsogon.gonggomoon.domain.strategy.content.PortfolioStrategyContent;

import java.util.List;

public interface PortfolioStrategyContentGenerator {
    public PortfolioStrategyContent generate(List<Experience> experiences, GeneratePortfolioStrategyRequest req);
}
