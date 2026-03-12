package com.sogonsogon.gonggomoon.domain.ai.domain;

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;



@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Experiences {
    @Builder
    public Experiences(List<ExperienceItem> experiences) {
        this.experiences = experiences;
    }

    private List<ExperienceItem> experiences = new ArrayList<>();

    public static Experiences of(List<ExperienceItem> experiences) {
        return Experiences.builder()
            .experiences(experiences)
            .build();
    }
}
