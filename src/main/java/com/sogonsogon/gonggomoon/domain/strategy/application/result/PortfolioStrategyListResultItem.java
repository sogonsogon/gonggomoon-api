package com.sogonsogon.gonggomoon.domain.strategy.application.result;

import com.sogonsogon.gonggomoon.domain.strategy.domain.IndustryType;
import com.sogonsogon.gonggomoon.domain.strategy.domain.JobType;
import com.sogonsogon.gonggomoon.domain.strategy.domain.PortfolioStrategy;
import lombok.Builder;

import java.time.Instant;

@Builder
public record PortfolioStrategyListResultItem(
        Long strategyId,
        JobType jobType,
        IndustryType industryType,
        Instant createdAt
) {
    public static PortfolioStrategyListResultItem from (PortfolioStrategy portfolioStrategy) {
        return PortfolioStrategyListResultItem.builder()
                .strategyId(portfolioStrategy.getId())
                .jobType(portfolioStrategy.getJobType())
                .industryType(portfolioStrategy.getIndustryType())
                .createdAt(portfolioStrategy.getCreatedAt())
                .build();
    }
}
