package com.sogonsogon.gonggomoon.domain.experience.application.result;

import com.sogonsogon.gonggomoon.domain.experience.domain.Experience;
import lombok.Builder;
import java.util.UUID;

@Builder
public record CreateExperienceResult(
        UUID experienceId
){
    public static CreateExperienceResult from (Experience experience) {
        return CreateExperienceResult.builder()
                .experienceId(experience.getPublicId())
                .build();
    }
}
