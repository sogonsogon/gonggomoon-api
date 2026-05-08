package com.sogonsogon.gonggomoon.domain.experience.application;

import com.sogonsogon.gonggomoon.domain.ai.application.AiUsageAvailability;
import com.sogonsogon.gonggomoon.domain.ai.application.AiUsagePolicyService;
import com.sogonsogon.gonggomoon.domain.ai.domain.AiUsageType;
import com.sogonsogon.gonggomoon.domain.experience.application.result.ExperienceExtractionAvailabilityResult;
import java.lang.reflect.Field;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExperienceExtractionAvailabilityServiceTest {

    @Mock
    private AiUsagePolicyService aiUsagePolicyService;

    @InjectMocks
    private ExperienceExtractionAvailabilityService experienceExtractionAvailabilityService;

    private static final Long USER_ID = 1L;

    @Nested
    @DisplayName("getAvailability")
    class GetAvailabilityTest {

        @Test
        @DisplayName("주간 제한이 켜져 있고 이번 주 사용 횟수가 0이면 생성 가능하다")
        void getAvailability_success_whenWeeklyLimitEnabled_andNotUsedThisWeek() throws Exception {
            // given
            setField(experienceExtractionAvailabilityService, "weeklyLimitEnabled", true);
            when(aiUsagePolicyService.getAvailability(USER_ID, AiUsageType.EXPERIENCE_EXTRACTION, true))
                    .thenReturn(new AiUsageAvailability(0, 7, true, true));

            // when
            ExperienceExtractionAvailabilityResult result =
                    experienceExtractionAvailabilityService.getAvailability(USER_ID);

            // then
            assertEquals(0, result.usedCount());
            assertEquals(7, result.limitCount());
            assertTrue(result.canGenerate());
            assertTrue(result.canRetry());

            verify(aiUsagePolicyService).getAvailability(USER_ID, AiUsageType.EXPERIENCE_EXTRACTION, true);
        }

        @Test
        @DisplayName("주간 제한이 켜져 있고 이번 주 사용 횟수가 7이면 생성할 수 없다")
        void getAvailability_success_whenWeeklyLimitEnabled_andLimitReached() throws Exception {
            // given
            setField(experienceExtractionAvailabilityService, "weeklyLimitEnabled", true);
            when(aiUsagePolicyService.getAvailability(USER_ID, AiUsageType.EXPERIENCE_EXTRACTION, true))
                    .thenReturn(new AiUsageAvailability(7, 7, false, true));

            // when
            ExperienceExtractionAvailabilityResult result =
                    experienceExtractionAvailabilityService.getAvailability(USER_ID);

            // then
            assertEquals(7, result.usedCount());
            assertEquals(7, result.limitCount());
            assertFalse(result.canGenerate());
            assertTrue(result.canRetry());

            verify(aiUsagePolicyService).getAvailability(USER_ID, AiUsageType.EXPERIENCE_EXTRACTION, true);
        }

        @Test
        @DisplayName("주간 제한이 꺼져 있으면 저장 이력과 관계없이 항상 생성 가능하다")
        void getAvailability_success_whenWeeklyLimitDisabled() throws Exception {
            // given
            setField(experienceExtractionAvailabilityService, "weeklyLimitEnabled", false);
            when(aiUsagePolicyService.getAvailability(USER_ID, AiUsageType.EXPERIENCE_EXTRACTION, false))
                    .thenReturn(new AiUsageAvailability(0, 7, true, true));

            // when
            ExperienceExtractionAvailabilityResult result =
                    experienceExtractionAvailabilityService.getAvailability(USER_ID);

            // then
            assertEquals(0, result.usedCount());
            assertEquals(7, result.limitCount());
            assertTrue(result.canGenerate());
            assertTrue(result.canRetry());

            verify(aiUsagePolicyService).getAvailability(USER_ID, AiUsageType.EXPERIENCE_EXTRACTION, false);
        }
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
