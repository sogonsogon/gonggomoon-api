package com.sogonsogon.gonggomoon.domain.ai.application;

public record AiUsageAvailability(
    int usedCount,
    int limitCount,
    boolean canGenerate,
    boolean canRetry
) {
}
