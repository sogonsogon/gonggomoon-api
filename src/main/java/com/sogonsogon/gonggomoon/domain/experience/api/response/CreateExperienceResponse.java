package com.sogonsogon.gonggomoon.domain.experience.api.response;

import com.sogonsogon.gonggomoon.domain.experience.application.result.CreateExperienceResult;
import lombok.Builder;

@Builder
public record CreateExperienceResponse(
        Long experienceId
) {
    public static CreateExperienceResponse from (CreateExperienceResult result) {
        return CreateExperienceResponse.builder()
                .experienceId(result.experienceId())
                .build();
    }
}
