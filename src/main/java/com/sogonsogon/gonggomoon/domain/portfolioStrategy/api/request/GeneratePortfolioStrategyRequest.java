package com.sogonsogon.gonggomoon.domain.portfolioStrategy.api.request;

import com.sogonsogon.gonggomoon.domain.portfolioStrategy.domain.JobType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record GeneratePortfolioStrategyRequest(
        @Schema(description = "지원 직무 (선택)", example = "BACKEND")
        JobType jobType,
        @Schema(description = "산업 ID (선택)", example = "3")
        Long industryId,
        @NotNull(message = "postAnalysisId는 필수입니다.") Long postAnalysisId,

        @Schema(description = "전략에 활용할 경험 ID 목록", example = "[10, 11, 12]", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "experienceIds는 필수입니다.") // 리스트 자체가 null 방지
        List<@NotNull(message = "experienceId는 null일 수 없습니다.") Long> experienceIds // [1, null] 방지
) {
}
