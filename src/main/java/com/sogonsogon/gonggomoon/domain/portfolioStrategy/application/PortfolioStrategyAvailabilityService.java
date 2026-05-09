package com.sogonsogon.gonggomoon.domain.portfolioStrategy.application;

import com.sogonsogon.gonggomoon.domain.ai.application.AiUsageAvailability;
import com.sogonsogon.gonggomoon.domain.ai.application.AiUsagePolicyService;
import com.sogonsogon.gonggomoon.domain.ai.domain.AiUsageType;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.application.result.PortfolioStrategyAvailabilityResult;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.application.support.PortfolioStrategyAvailabilityCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PortfolioStrategyAvailabilityService {

    private final PortfolioStrategyAvailabilityCalculator portfolioStrategyAvailabilityCalculator;
    private final AiUsagePolicyService aiUsagePolicyService;

    @Value("${strategy.portfolio.weekly-limit-enabled:true}")
    private boolean weeklyLimitEnabled;

    public PortfolioStrategyAvailabilityResult getAvailability(Long userId) {
        AiUsageAvailability availability = aiUsagePolicyService.getAvailability(
                userId,
                AiUsageType.PORTFOLIO_STRATEGY,
                weeklyLimitEnabled
        );

        return portfolioStrategyAvailabilityCalculator.calculate (
                availability.usedCount(),
                availability.limitCount(),
                weeklyLimitEnabled
        );
    }
}
