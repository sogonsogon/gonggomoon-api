package com.sogonsogon.gonggomoon.domain.portfolioStrategy.application;

import com.sogonsogon.gonggomoon.domain.ai.application.AiUsageAvailability;
import com.sogonsogon.gonggomoon.domain.ai.application.AiUsagePolicyService;
import com.sogonsogon.gonggomoon.domain.ai.domain.AiUsageType;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.application.result.PortfolioStrategyAvailabilityResult;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.application.support.PortfolioStrategyAvailabilityCalculator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PortfolioStrategyAvailabilityServiceTest {

    @Mock
    private PortfolioStrategyAvailabilityCalculator portfolioStrategyAvailabilityCalculator;

    @Mock
    private AiUsagePolicyService aiUsagePolicyService;

    @InjectMocks
    private PortfolioStrategyAvailabilityService portfolioStrategyAvailabilityService;

    private static final Long USER_ID = 1L;

    @Nested
    @DisplayName("getAvailability")
    class GetAvailabilityTest {

        @Test
        @DisplayName("주간 제한이 켜져 있고 이번 주 성공 횟수가 0이면 생성 가능하다")
        void getAvailability_success_whenWeeklyLimitEnabled_andNotUsedThisWeek() throws Exception {
            // given - weeklyLimitEnabled를 true로 바꿔주는 코드
            setField(portfolioStrategyAvailabilityService, "weeklyLimitEnabled", true);
            when(aiUsagePolicyService.getAvailability(USER_ID, AiUsageType.PORTFOLIO_STRATEGY, true))
                    .thenReturn(new AiUsageAvailability(0, 7, true, true));
            when(portfolioStrategyAvailabilityCalculator.calculate(eq(0), eq(7), eq(true)))
                    .thenReturn(PortfolioStrategyAvailabilityResult.of(0, 7, true));

            // when
            PortfolioStrategyAvailabilityResult result = portfolioStrategyAvailabilityService.getAvailability(USER_ID);

            // then
            assertEquals(0, result.usedCount());
            assertEquals(7, result.limitCount());
            assertTrue(result.canGenerate());

            verify(aiUsagePolicyService).getAvailability(USER_ID, AiUsageType.PORTFOLIO_STRATEGY, true);
            verify(portfolioStrategyAvailabilityCalculator).calculate(eq(0), eq(7), eq(true));
        }

        @Test
        @DisplayName("주간 제한이 켜져 있고 이번 주 성공 횟수가 7이면 생성할 수 없다")
        void getAvailability_success_whenWeeklyLimitEnabled_andLimitReached() throws Exception {
            // given
            setField(portfolioStrategyAvailabilityService, "weeklyLimitEnabled", true);
            when(aiUsagePolicyService.getAvailability(USER_ID, AiUsageType.PORTFOLIO_STRATEGY, true))
                    .thenReturn(new AiUsageAvailability(7, 7, false, true));
            when(portfolioStrategyAvailabilityCalculator.calculate(eq(7), eq(7), eq(true)))
                    .thenReturn(PortfolioStrategyAvailabilityResult.of(7, 7, false));

            // when
            PortfolioStrategyAvailabilityResult result = portfolioStrategyAvailabilityService.getAvailability(USER_ID);

            // then
            assertEquals(7, result.usedCount());
            assertEquals(7, result.limitCount());
            assertFalse(result.canGenerate());

            verify(aiUsagePolicyService).getAvailability(USER_ID, AiUsageType.PORTFOLIO_STRATEGY, true);
            verify(portfolioStrategyAvailabilityCalculator).calculate(eq(7), eq(7), eq(true));
        }

        @Test
        @DisplayName("주간 제한이 꺼져 있으면 저장 이력과 관계없이 항상 생성 가능하다")
        void getAvailability_success_whenWeeklyLimitDisabled() throws Exception {
            // given
            setField(portfolioStrategyAvailabilityService, "weeklyLimitEnabled", false);
            when(aiUsagePolicyService.getAvailability(USER_ID, AiUsageType.PORTFOLIO_STRATEGY, false))
                    .thenReturn(new AiUsageAvailability(0, 7, true, true));
            when(portfolioStrategyAvailabilityCalculator.calculate(eq(0), eq(7), eq(false)))
                    .thenReturn(PortfolioStrategyAvailabilityResult.of(0, 7, true));

            // when
            PortfolioStrategyAvailabilityResult result = portfolioStrategyAvailabilityService.getAvailability(USER_ID);

            // then
            assertEquals(0, result.usedCount());
            assertEquals(7, result.limitCount());
            assertTrue(result.canGenerate());

            verify(aiUsagePolicyService).getAvailability(USER_ID, AiUsageType.PORTFOLIO_STRATEGY, false);
            verify(portfolioStrategyAvailabilityCalculator).calculate(eq(0), eq(7), eq(false));
        }
    }



    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
