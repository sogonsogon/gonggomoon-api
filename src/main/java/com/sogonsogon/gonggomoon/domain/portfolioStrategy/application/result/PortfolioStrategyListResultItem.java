package com.sogonsogon.gonggomoon.domain.portfolioStrategy.application.result;

import com.sogonsogon.gonggomoon.domain.portfolioStrategy.domain.JobType;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.domain.PortfolioStrategyGenerateStatus;
import lombok.Builder;

import java.time.Instant;

@Builder
public record PortfolioStrategyListResultItem(
        Long strategyId,
        Long postId,
        Long postAnalysisId,
        String postAnalysisTitle,
        JobType jobType,
        String industryName,
        PortfolioStrategyGenerateStatus status,
        Instant createdAt
) {
}
