package com.sogonsogon.gonggomoon.domain.ai.application;

import com.sogonsogon.gonggomoon.domain.ai.domain.AiUsageCounter;
import com.sogonsogon.gonggomoon.domain.ai.domain.AiUsageCounterRepository;
import com.sogonsogon.gonggomoon.domain.ai.domain.AiUsageType;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AiUsagePolicyService {

    private static final int WEEKLY_LIMIT_COUNT = 7;
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private final AiUsageCounterRepository aiUsageCounterRepository;

    @Transactional
    public boolean reserve(Long userId, AiUsageType usageType) {
        LocalDate weekStartDate = currentWeekStartDate();
        ensureCounterExists(userId, usageType, weekStartDate);

        return aiUsageCounterRepository.incrementIfBelowLimit(
            userId,
            usageType,
            weekStartDate,
            WEEKLY_LIMIT_COUNT
        ) == 1;
    }

    @Transactional
    public void refund(Long userId, AiUsageType usageType, LocalDate generatedDate) {
        LocalDate weekStartDate = weekStartDateOf(generatedDate);
        aiUsageCounterRepository.decrementIfPositive(userId, usageType, weekStartDate);
    }

    @Transactional(readOnly = true)
    public AiUsageAvailability getAvailability(Long userId, AiUsageType usageType, boolean limitEnabled) {
        LocalDate weekStartDate = currentWeekStartDate();

        int usedCount = 0;
        if (limitEnabled) {
            usedCount = aiUsageCounterRepository.findByUserIdAndUsageTypeAndWeekStartDate(userId, usageType, weekStartDate)
                .map(AiUsageCounter::getUsageCount)
                .orElse(0);
        }

        boolean canGenerate = true;
        if (limitEnabled) {
            canGenerate = usedCount < WEEKLY_LIMIT_COUNT;
        }

        return new AiUsageAvailability(
            usedCount,
            WEEKLY_LIMIT_COUNT,
            canGenerate,
            true
        );
    }

    public LocalDate currentWeekStartDate() {
        return weekStartDateOf(Instant.now().atZone(KST).toLocalDate());
    }

    public LocalDate weekStartDateOf(LocalDate date) {
        return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    private void ensureCounterExists(Long userId, AiUsageType usageType, LocalDate weekStartDate) {
        if (aiUsageCounterRepository.findByUserIdAndUsageTypeAndWeekStartDate(userId, usageType, weekStartDate).isPresent()) {
            return;
        }

        try {
            aiUsageCounterRepository.save(AiUsageCounter.create(userId, usageType, weekStartDate));
        } catch (DataIntegrityViolationException ignored) {
            // Another concurrent request created the same weekly counter first.
        }
    }
}
