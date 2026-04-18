package com.sogonsogon.gonggomoon.domain.portfolioStrategy.content;

/**
 * 경험 정렬 전략
 */
public record ExperienceOrderingItem(
        int order,
        String title,
        String reason
) {
}
