package com.sogonsogon.gonggomoon.domain.portfolioStrategy.application.result;

import com.sogonsogon.gonggomoon.domain.portfolioStrategy.domain.JobType;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.domain.PortfolioStrategyGenerateStatus;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public record PortfolioStrategyListResultItem(
        UUID strategyId,
        UUID postId,
        UUID postAnalysisId,
        String postAnalysisTitle,
        JobType jobType,
        String industryName,
        PortfolioStrategyGenerateStatus status,
        Instant createdAt
) {
}
