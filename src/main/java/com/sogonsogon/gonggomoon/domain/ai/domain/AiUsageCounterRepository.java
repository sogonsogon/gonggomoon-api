package com.sogonsogon.gonggomoon.domain.ai.domain;

import java.time.LocalDate;
import java.util.Optional;

public interface AiUsageCounterRepository {

    AiUsageCounter save(AiUsageCounter counter);

    Optional<AiUsageCounter> findByUserIdAndUsageTypeAndWeekStartDate(
        Long userId,
        AiUsageType usageType,
        LocalDate weekStartDate
    );

    int incrementIfBelowLimit(
        Long userId,
        AiUsageType usageType,
        LocalDate weekStartDate,
        int limitCount
    );

    int decrementIfPositive(
        Long userId,
        AiUsageType usageType,
        LocalDate weekStartDate
    );
}
