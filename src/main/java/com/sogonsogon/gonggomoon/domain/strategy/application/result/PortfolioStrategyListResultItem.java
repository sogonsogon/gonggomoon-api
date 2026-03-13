package com.sogonsogon.gonggomoon.domain.strategy.application.result;

import com.sogonsogon.gonggomoon.domain.strategy.domain.JobType;
import com.sogonsogon.gonggomoon.domain.strategy.domain.PortfolioStrategy;
import lombok.Builder;

import java.time.Instant;

@Builder
public record PortfolioStrategyListResultItem(
        Long strategyId,
        JobType jobType,
        Long industryId,
        Instant createdAt
) {
    public static PortfolioStrategyListResultItem from (PortfolioStrategy portfolioStrategy) {
        return PortfolioStrategyListResultItem.builder()
                .strategyId(portfolioStrategy.getId())
                .jobType(portfolioStrategy.getJobType())
                .industryId(portfolioStrategy.getIndustryId())
                .createdAt(portfolioStrategy.getCreatedAt())
                .build();
    }
}
