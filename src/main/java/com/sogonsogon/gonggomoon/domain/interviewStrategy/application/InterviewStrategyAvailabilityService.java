package com.sogonsogon.gonggomoon.domain.interviewStrategy.application;

import com.sogonsogon.gonggomoon.domain.interviewStrategy.application.result.InterviewStrategyAvailabilityResult;
import com.sogonsogon.gonggomoon.domain.interviewStrategy.application.support.InterviewStrategyAvailabilityCalculator;
import com.sogonsogon.gonggomoon.domain.interviewStrategy.domain.InterviewStrategyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class InterviewStrategyAvailabilityService {

    private final InterviewStrategyRepository interviewStrategyRepository;
    private final InterviewStrategyAvailabilityCalculator interviewStrategyAvailabilityCalculator;

    @Value("${strategy.interview.daily-limit-enabled:true}")
    private boolean dailyLimitEnabled;

    private static final int DAILY_LIMIT_COUNT = 1;
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    public InterviewStrategyAvailabilityResult getAvailability(Long userId) {
        Instant now = Instant.now();
        LocalDate today = now.atZone(KST).toLocalDate();

        int usedCount = dailyLimitEnabled
                ? interviewStrategyRepository.countByUserIdAndGeneratedDate(userId, today)
                : 0;
        return interviewStrategyAvailabilityCalculator.calculate(
                usedCount,
                DAILY_LIMIT_COUNT,
                dailyLimitEnabled
        );
    }
}
