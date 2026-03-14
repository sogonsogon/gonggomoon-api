package com.sogonsogon.gonggomoon.domain.strategy.application;

import com.sogonsogon.gonggomoon.domain.strategy.application.result.StrategyAvailabilityResult;
import com.sogonsogon.gonggomoon.domain.strategy.application.support.StrategyAvailabilityCalculator;
import com.sogonsogon.gonggomoon.domain.strategy.domain.InterviewStrategyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InterviewStrategyAvailabilityServiceTest {

    @Mock
    private InterviewStrategyRepository interviewStrategyRepository;

    @Mock
    private StrategyAvailabilityCalculator strategyAvailabilityCalculator;

    @InjectMocks
    private InterviewStrategyAvailabilityService interviewStrategyAvailabilityService;

    private static final Long USER_ID = 1L;

    @Nested
    @DisplayName("getAvailability")
    class GetAvailabilityTest {
        @Test
        @DisplayName("일일 제한이 켜져 있고 오늘 사용 횟수가 0이면 생성 가능하다")
        void getAvailability_success_whenDailyLimitEnabled_andNotUsedToday() throws Exception {
            // given - dailyLimitEnabled를 true로 바꿔주는 코드
            setField(interviewStrategyAvailabilityService, "dailyLimitEnabled", true);

            // countByUserIdAndGeneratedDate가 호출되면 오늘 사용 횟수를 0으로 돌려준다.
            when(interviewStrategyRepository.countByUserIdAndGeneratedDate(eq(USER_ID), any(LocalDate.class)))
                    .thenReturn(0);
            when(strategyAvailabilityCalculator.calculate(0, 1, true))
                    .thenReturn(StrategyAvailabilityResult.of(0, 1, true));

            // when
            StrategyAvailabilityResult result = interviewStrategyAvailabilityService.getAvailability(USER_ID);

            // then
            assertEquals(0, result.usedCount());
            assertEquals(1, result.limitCount());
            assertTrue(result.canGenerate());

            verify(interviewStrategyRepository)
                    .countByUserIdAndGeneratedDate(eq(USER_ID), any(LocalDate.class));
            verify(strategyAvailabilityCalculator).calculate(0, 1, true);
        }

        @Test
        @DisplayName("일일 제한이 켜져 있고 오늘 이미 1회 사용했으면 생성할 수 없다")
        void getAvailability_success_whenDailyLimitEnabled_andAlreadyUsedToday() throws Exception {
            // given
            setField(interviewStrategyAvailabilityService, "dailyLimitEnabled", true);

            // 오늘 이미 한번 생성한 상태를 가짜로 만들어준다. (usedCount = 1)
            when(interviewStrategyRepository.countByUserIdAndGeneratedDate(eq(USER_ID), any(LocalDate.class)))
                    .thenReturn(1);
            when(strategyAvailabilityCalculator.calculate(1, 1, true))
                    .thenReturn(StrategyAvailabilityResult.of(1, 1, false));

            // when
            StrategyAvailabilityResult result = interviewStrategyAvailabilityService.getAvailability(USER_ID);

            // then
            assertEquals(1, result.usedCount());
            assertEquals(1, result.limitCount());
            assertFalse(result.canGenerate());

            verify(interviewStrategyRepository)
                    .countByUserIdAndGeneratedDate(eq(USER_ID), any(LocalDate.class));
            verify(strategyAvailabilityCalculator).calculate(1, 1, true);
        }

        @Test
        @DisplayName("일일 제한이 꺼져 있으면 저장 이력과 관계없이 항상 생성 가능하다")
        void getAvailability_success_whenDailyLimitDisabled() throws Exception {
            // given
            setField(interviewStrategyAvailabilityService, "dailyLimitEnabled", false);

            when(strategyAvailabilityCalculator.calculate(0, 1, false))
                    .thenReturn(StrategyAvailabilityResult.of(0, 1, true));

            // when
            StrategyAvailabilityResult result = interviewStrategyAvailabilityService.getAvailability(USER_ID);

            // then
            assertEquals(0, result.usedCount());
            assertEquals(1, result.limitCount());
            assertTrue(result.canGenerate());

            verifyNoInteractions(interviewStrategyRepository);
            verify(strategyAvailabilityCalculator).calculate(0, 1, false);
        }
    }
    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
