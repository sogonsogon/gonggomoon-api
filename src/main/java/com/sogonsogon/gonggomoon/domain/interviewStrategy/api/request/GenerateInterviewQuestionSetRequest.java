package com.sogonsogon.gonggomoon.domain.interviewStrategy.api.request;

import jakarta.validation.constraints.NotNull;

public record GenerateInterviewQuestionSetRequest(
        @NotNull(message = "파일 선택은 필수입니다.")
        Long fileAssetId
) {
}
