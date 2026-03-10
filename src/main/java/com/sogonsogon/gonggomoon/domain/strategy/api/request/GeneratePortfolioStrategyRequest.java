package com.sogonsogon.gonggomoon.domain.strategy.api.request;

import com.sogonsogon.gonggomoon.domain.strategy.domain.IndustryType;
import com.sogonsogon.gonggomoon.domain.strategy.domain.JobType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record GeneratePortfolioStrategyRequest(
        @NotNull(message = "직무 선택은 필수입니다.") JobType jobType,
        IndustryType industryType,

        @NotNull(message = "experienceIds는 필수입니다.") // 리스트 자체가 null 방지
        List<@NotNull(message = "experienceId는 null일 수 없습니다.") Long> experienceIds // [1, null] 방지
) {
}
