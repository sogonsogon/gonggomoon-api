package com.sogonsogon.gonggomoon.domain.strategy.application.result;

import com.sogonsogon.gonggomoon.domain.strategy.domain.JobType;
import lombok.Builder;

import java.time.Instant;

@Builder
public record PortfolioStrategyListResultItem(
        Long strategyId,
        JobType jobType,
        String industryName,
        Instant createdAt
) {
}
