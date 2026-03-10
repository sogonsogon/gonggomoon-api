package com.sogonsogon.gonggomoon.domain.strategy.content;

import com.sogonsogon.gonggomoon.domain.experience.domain.ExperienceType;

/**
 * 경험별 전략 포인트
 */
public record ExperienceStrategyPoint(
        ExperienceType experienceType,
        String experienceTitle,
        String strategyPoint
) {

}
