package com.sogonsogon.gonggomoon.domain.strategy.content;

import lombok.Builder;

import java.util.List;

/**
 * 화면에 보여주는 전략 내용 (JSON 본문)
 * @param mainPositioningMessage
 * @param experienceStrategyPoints
 * @param experienceOrdering
 * @param keywords
 * @param strengths
 * @param kpiCheckList
 * @param improvementGuides
 */
@Builder
public record PortfolioStrategyContent(
        String mainPositioningMessage, // 포지셔닝 메시지
        List<ExperienceStrategyPoint> experienceStrategyPoints, // 경험별 전략 포인트
        List<ExperienceOrderingItem> experienceOrdering, // 경험 정렬 전략
        List<String> keywords, // 강조 키워드
        List<String> strengths, // 강조 역량
        List<String> kpiCheckList, // KPI 체크리스트
        List<ImprovementGuide> improvementGuides // 보완 가이드
) {
    public static PortfolioStrategyContent of (
            String mainPositioningMessage,
            List<ExperienceStrategyPoint> experienceStrategyPoints,
            List<ExperienceOrderingItem> experienceOrdering,
            List<String> keywords,
            List<String> strengths,
            List<String> kpiCheckList,
            List<ImprovementGuide> improvementGuides
    ) {
        return PortfolioStrategyContent.builder()
                .mainPositioningMessage(mainPositioningMessage)
                .experienceStrategyPoints(experienceStrategyPoints)
                .experienceOrdering(experienceOrdering)
                .keywords(keywords)
                .strengths(strengths)
                .kpiCheckList(kpiCheckList)
                .improvementGuides(improvementGuides)
                .build();
    }
}
