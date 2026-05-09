package com.sogonsogon.gonggomoon.domain.ai.dto.response;
import java.util.List;

public record ExperienceExtractResponse(
    List<Long> extractedExperienceIds
) {
}
