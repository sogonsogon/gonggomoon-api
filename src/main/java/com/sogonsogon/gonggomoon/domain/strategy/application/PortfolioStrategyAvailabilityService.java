package com.sogonsogon.gonggomoon.domain.strategy.application;

import com.sogonsogon.gonggomoon.domain.strategy.application.result.StrategyAvailabilityResult;
import com.sogonsogon.gonggomoon.domain.strategy.application.support.StrategyAvailabilityCalculator;
import com.sogonsogon.gonggomoon.domain.strategy.domain.PortfolioStrategyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class PortfolioStrategyAvailabilityService {

    private final PortfolioStrategyRepository portfolioStrategyRepository;
    private final StrategyAvailabilityCalculator strategyAvailabilityCalculator;

    @Value("${strategy.portfolio.daily-limit-enabled:true}")
    private boolean dailyLimitEnabled;

    private static final int DAILY_LIMIT_COUNT = 1;
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    public StrategyAvailabilityResult getAvailability(Long userId) {
        Instant now = Instant.now();
        LocalDate today = now.atZone(KST).toLocalDate();

        int usedCount = dailyLimitEnabled
                ? portfolioStrategyRepository.countByUserIdAndGeneratedDate(userId, today)
                : 0;

        return strategyAvailabilityCalculator.calculate (
                usedCount,
                DAILY_LIMIT_COUNT,
                dailyLimitEnabled
        );
    }
}
