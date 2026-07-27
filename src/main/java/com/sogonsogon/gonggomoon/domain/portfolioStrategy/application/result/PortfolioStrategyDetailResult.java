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
import java.util.UUID;

@Builder
public record PortfolioStrategyDetailResult(
        UUID strategyId,
        UUID postId,
        UUID postAnalysisId,
        String postAnalysisTitle,
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
            UUID postId,
            UUID postAnalysisId,
            String postAnalysisTitle,
            PortfolioStrategyContent content,
            String industryName
    ) {
        return PortfolioStrategyDetailResult.builder()
                .strategyId(portfolioStrategy.getPublicId())
                .postId(postId)
                .postAnalysisId(postAnalysisId)
                .postAnalysisTitle(postAnalysisTitle)
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
