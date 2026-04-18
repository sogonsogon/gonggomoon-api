package com.sogonsogon.gonggomoon.domain.portfolioStrategy.application.result;

import com.sogonsogon.gonggomoon.domain.portfolioStrategy.content.ExperienceOrderingItem;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.content.ExperienceStrategyPoint;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.content.ImprovementGuide;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.content.PortfolioStrategyContent;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.domain.JobType;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.domain.PortfolioStrategy;
import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
public record PortfolioStrategyDetailResult(
        Long strategyId,
        JobType jobType,
        String industryName,
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
            PortfolioStrategyContent content,
            String industryName
    ) {
        return PortfolioStrategyDetailResult.builder()
                .strategyId(portfolioStrategy.getId())
                .jobType(portfolioStrategy.getJobType())
                .industryName(industryName)
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
