package com.sogonsogon.gonggomoon.domain.post.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record PostAnalysisRequest(

        @Schema(description = "분석할 채용 공고 URL", example = "https://careers.example.com/jobs/12345", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "URL은 필수입니다.")
        String postUrl
) {
}
