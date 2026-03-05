package com.sogonsogon.gonggomoon.domain.experience.application.result;

import com.sogonsogon.gonggomoon.domain.experience.domain.Experience;
import lombok.Builder;

@Builder
public record CreateExperienceResult(
        Long experienceId
){
    public static CreateExperienceResult from (Experience experience) {
        return CreateExperienceResult.builder()
                .experienceId(experience.getId())
                .build();
    }
}