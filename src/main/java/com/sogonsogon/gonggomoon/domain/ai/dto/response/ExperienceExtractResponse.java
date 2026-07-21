package com.sogonsogon.gonggomoon.domain.ai.dto.response;
import java.util.List;
import java.util.UUID;

public record ExperienceExtractResponse(
    List<UUID> extractedExperienceIds
) {
}
