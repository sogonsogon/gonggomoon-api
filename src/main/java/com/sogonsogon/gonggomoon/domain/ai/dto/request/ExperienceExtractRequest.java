package com.sogonsogon.gonggomoon.domain.ai.dto.request;

import java.util.List;

public record ExperienceExtractRequest(
    Long userId,
    List<Long> fileAssetIds
) {
}
