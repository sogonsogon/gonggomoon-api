package com.sogonsogon.gonggomoon.domain.strategy.application.result;

import com.sogonsogon.gonggomoon.domain.strategy.content.ExperienceOrderingItem;
import com.sogonsogon.gonggomoon.domain.strategy.content.ExperienceStrategyPoint;
import com.sogonsogon.gonggomoon.domain.strategy.content.ImprovementGuide;
import com.sogonsogon.gonggomoon.domain.strategy.content.PortfolioStrategyContent;
import com.sogonsogon.gonggomoon.domain.strategy.domain.IndustryType;
import com.sogonsogon.gonggomoon.domain.strategy.domain.JobType;
import com.sogonsogon.gonggomoon.domain.strategy.domain.PortfolioStrategy;
import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
public record PortfolioStrategyDetailResult(
        Long strategyId,
        JobType jobType,
        IndustryType industryType,
        int selectedExperienceCount,
        Instant createdAt,
        String mainPositioningMessage,
        List<ExperienceStrategyPoint> experienceStrategyPoints,
        List<ExperienceOrderingItem> experienceOrdering,
        List<String> keywords,
        List<String> strengths,
        List<String> kpiCheckList,
        List<ImprovementGuide> improvementGuides
) {
    public static PortfolioStrategyDetailResult of (
            PortfolioStrategy portfolioStrategy,
            PortfolioStrategyContent content
    ) {
        return PortfolioStrategyDetailResult.builder()
                .strategyId(portfolioStrategy.getId())
                .jobType(portfolioStrategy.getJobType())
                .industryType(portfolioStrategy.getIndustryType())
                .selectedExperienceCount(portfolioStrategy.getSelectedExperienceCount())
                .createdAt(portfolioStrategy.getCreatedAt())
                .mainPositioningMessage(content.mainPositioningMessage())
                .experienceStrategyPoints(content.experienceStrategyPoints())
                .experienceOrdering(content.experienceOrdering())
                .keywords(content.keywords())
                .strengths(content.strengths())
                .kpiCheckList(content.kpiCheckList())
                .improvementGuides(content.improvementGuides())
                .build();
    }
}
