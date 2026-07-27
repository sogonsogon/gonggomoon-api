package com.sogonsogon.gonggomoon.domain.portfolioStrategy.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record GeneratePortfolioStrategyRequest(
        @NotNull(message = "postAnalysisId는 필수입니다.") UUID postAnalysisId,

        @Schema(description = "전략에 활용할 경험 ID 목록", example = "[10, 11, 12]", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "experienceIds는 필수입니다.") // 리스트 자체가 null 방지
        List<@NotNull(message = "experienceId는 null일 수 없습니다.") UUID> experienceIds // [id, null] 방지
) {
}
