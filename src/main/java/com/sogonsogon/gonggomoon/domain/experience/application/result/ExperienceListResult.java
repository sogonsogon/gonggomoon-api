package com.sogonsogon.gonggomoon.domain.experience.application.result;

import com.sogonsogon.gonggomoon.domain.experience.api.response.ExperienceListResultItem;
import com.sogonsogon.gonggomoon.domain.experience.domain.Experience;
import lombok.Builder;

import java.util.List;

@Builder
public record ExperienceListResult(
        int totalCount,
        List<ExperienceListResultItem> contents
){
    public static ExperienceListResult from(List<Experience> experiences) {
        List<ExperienceListResultItem> items = experiences.stream()
                .map(ExperienceListResultItem::from)
                .toList();

        return ExperienceListResult.builder()
                .totalCount(items.size())
                .contents(items)
                .build();
    }
}
