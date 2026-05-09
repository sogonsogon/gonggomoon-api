package com.sogonsogon.gonggomoon.domain.experience.api.request;

import com.sogonsogon.gonggomoon.domain.experience.domain.DocumentCategory;
import jakarta.validation.constraints.NotNull;

public record ImportExperienceRequest(
        @NotNull(message = "파일 카테고리는 필수입니다.") DocumentCategory category
) {
}
