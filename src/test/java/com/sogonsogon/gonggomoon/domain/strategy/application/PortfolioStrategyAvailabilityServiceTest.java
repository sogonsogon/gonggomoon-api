package com.sogonsogon.gonggomoon.domain.strategy.application;

import com.sogonsogon.gonggomoon.domain.strategy.application.result.StrategyAvailabilityResult;
import com.sogonsogon.gonggomoon.domain.strategy.application.support.StrategyAvailabilityCalculator;
import com.sogonsogon.gonggomoon.domain.strategy.domain.PortfolioStrategyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PortfolioStrategyAvailabilityServiceTest {

    @Mock
    private PortfolioStrategyRepository portfolioStrategyRepository;

    @Mock
    private StrategyAvailabilityCalculator strategyAvailabilityCalculator;

    @InjectMocks
    private PortfolioStrategyAvailabilityService strategyAvailabilityService;

    private static final Long USER_ID = 1L;

    @Nested
    @DisplayName("getAvailability")
    class GetAvailabilityTest {

        @Test
        @DisplayName("일일 제한이 켜져 있고 오늘 사용 횟수가 0이면 생성 가능하다")
        void getAvailability_success_whenDailyLimitEnabled_andNotUsedToday() throws Exception {
            // given - dailyLimitEnabled를 true로 바꿔주는 코드
            setField(strategyAvailabilityService, "dailyLimitEnabled", true);

            // countByUserIdAndGeneratedDate가 호출되면 오늘 사용 횟수를 0으로 돌려준다.
            when(portfolioStrategyRepository.countByUserIdAndGeneratedDate(eq(USER_ID), any(LocalDate.class)))
                    .thenReturn(0);
            when(strategyAvailabilityCalculator.calculate(0, 1, true))
                    .thenReturn(StrategyAvailabilityResult.of(0, 1, true));

            // when
            StrategyAvailabilityResult result = strategyAvailabilityService.getAvailability(USER_ID);

            // then
            assertEquals(0, result.usedCount());
            assertEquals(1, result.limitCount());
            assertTrue(result.canGenerate());

            verify(portfolioStrategyRepository)
                    .countByUserIdAndGeneratedDate(eq(USER_ID), any(LocalDate.class));
            verify(strategyAvailabilityCalculator).calculate(0, 1, true);
        }

        @Test
        @DisplayName("일일 제한이 켜져 있고 오늘 이미 1회 사용했으면 생성할 수 없다")
        void getAvailability_success_whenDailyLimitEnabled_andAlreadyUsedToday() throws Exception {
            // given
            setField(strategyAvailabilityService, "dailyLimitEnabled", true);

            // 오늘 이미 한번 생성한 상태를 가짜로 만들어준다. (usedCount = 1)
            when(portfolioStrategyRepository.countByUserIdAndGeneratedDate(eq(USER_ID), any(LocalDate.class)))
                    .thenReturn(1);
            when(strategyAvailabilityCalculator.calculate(1, 1, true))
                    .thenReturn(StrategyAvailabilityResult.of(1, 1, false));

            // when
            StrategyAvailabilityResult result = strategyAvailabilityService.getAvailability(USER_ID);

            // then
            assertEquals(1, result.usedCount());
            assertEquals(1, result.limitCount());
            assertFalse(result.canGenerate());

            verify(portfolioStrategyRepository)
                    .countByUserIdAndGeneratedDate(eq(USER_ID), any(LocalDate.class));
            verify(strategyAvailabilityCalculator).calculate(1, 1, true);
        }

        @Test
        @DisplayName("일일 제한이 꺼져 있으면 저장 이력과 관계없이 항상 생성 가능하다")
        void getAvailability_success_whenDailyLimitDisabled() throws Exception {
            // given
            setField(strategyAvailabilityService, "dailyLimitEnabled", false);

            when(strategyAvailabilityCalculator.calculate(0, 1, false))
                    .thenReturn(StrategyAvailabilityResult.of(0, 1, true));

            // when
            StrategyAvailabilityResult result = strategyAvailabilityService.getAvailability(USER_ID);

            // then
            assertEquals(0, result.usedCount());
            assertEquals(1, result.limitCount());
            assertTrue(result.canGenerate());

            verifyNoInteractions(portfolioStrategyRepository);
            verify(strategyAvailabilityCalculator).calculate(0, 1, false);
        }
    }



    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
