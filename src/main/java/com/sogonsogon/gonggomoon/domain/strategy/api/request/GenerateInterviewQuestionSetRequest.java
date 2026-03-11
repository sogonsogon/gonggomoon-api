package com.sogonsogon.gonggomoon.domain.strategy.api.request;

import jakarta.validation.constraints.NotNull;

public record GenerateInterviewQuestionSetRequest(
        @NotNull(message = "파일 선택은 필수입니다.")
        Long fileAssetId
) {
}
