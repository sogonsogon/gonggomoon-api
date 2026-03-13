package com.sogonsogon.gonggomoon.domain.strategy.api.response;

import com.sogonsogon.gonggomoon.domain.strategy.application.result.PortfolioStrategyDetailResult;
import com.sogonsogon.gonggomoon.domain.strategy.content.ExperienceOrderingItem;
import com.sogonsogon.gonggomoon.domain.strategy.content.ExperienceStrategyPoint;
import com.sogonsogon.gonggomoon.domain.strategy.content.ImprovementGuide;
import com.sogonsogon.gonggomoon.domain.strategy.domain.JobType;
import lombok.Builder;

import java.time.Instant;
import java.util.List;

@Builder
public record PortfolioStrategyDetailResponse(
        Long strategyId,
        JobType jobType,
        Long industryId,
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
    public static PortfolioStrategyDetailResponse from (PortfolioStrategyDetailResult result) {
        return PortfolioStrategyDetailResponse.builder()
                .strategyId(result.strategyId())
                .jobType(result.jobType())
                .industryId(result.industryId())
                .selectedExperienceCount(result.selectedExperienceCount())
                .createdAt(result.createdAt())
                .mainPositioningMessage(result.mainPositioningMessage())
                .experienceStrategyPoints(result.experienceStrategyPoints())
                .experienceOrdering(result.experienceOrdering())
                .keywords(result.keywords())
                .strengths(result.strengths())
                .kpiCheckList(result.kpiCheckList())
                .improvementGuides(result.improvementGuides())
                .build();
    }
}