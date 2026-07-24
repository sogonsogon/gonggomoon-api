package com.sogonsogon.gonggomoon.domain.portfolioStrategy.application.result;

import com.sogonsogon.gonggomoon.domain.portfolioStrategy.domain.JobType;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.domain.PortfolioStrategyGenerateStatus;

import java.time.Instant;
import java.util.UUID;

public record PortfolioStrategyListQueryItem(
        Long id,
        UUID strategyId,
        UUID postId,
        UUID postAnalysisId,
        String postAnalysisTitle,
        JobType jobType,
        String industryName,
        PortfolioStrategyGenerateStatus status,
        Instant createdAt
) {
    public PortfolioStrategyListResultItem toResultItem() {
        return PortfolioStrategyListResultItem.builder()
                .strategyId(strategyId)
                .postId(postId)
                .postAnalysisId(postAnalysisId)
                .postAnalysisTitle(postAnalysisTitle)
                .jobType(jobType)
                .industryName(industryName)
                .status(status)
                .createdAt(createdAt)
                .build();
    }
}
