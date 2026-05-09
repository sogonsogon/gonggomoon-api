package com.sogonsogon.gonggomoon.domain.experience.application;

import com.sogonsogon.gonggomoon.domain.ai.application.AiUsageAvailability;
import com.sogonsogon.gonggomoon.domain.ai.application.AiUsagePolicyService;
import com.sogonsogon.gonggomoon.domain.ai.domain.AiUsageType;
import com.sogonsogon.gonggomoon.domain.experience.application.result.ExperienceExtractionAvailabilityResult;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExperienceExtractionAvailabilityService {

    private final AiUsagePolicyService aiUsagePolicyService;

    @Value("${experience.extraction.weekly-limit-enabled:true}")
    private boolean weeklyLimitEnabled;

    public ExperienceExtractionAvailabilityResult getAvailability(Long userId) {
        AiUsageAvailability availability = aiUsagePolicyService.getAvailability(
                userId,
                AiUsageType.EXPERIENCE_EXTRACTION,
                weeklyLimitEnabled
        );

        return new ExperienceExtractionAvailabilityResult(
                availability.usedCount(),
                availability.limitCount(),
                availability.canGenerate(),
                availability.canRetry()
        );
    }
}
