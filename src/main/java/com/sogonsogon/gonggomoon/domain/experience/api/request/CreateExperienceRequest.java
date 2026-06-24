package com.sogonsogon.gonggomoon.domain.experience.api.request;

import com.sogonsogon.gonggomoon.domain.experience.domain.ExperienceType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

public record CreateExperienceRequest(
        @Schema(description = "경험 제목", example = "백엔드 동아리 프로젝트 리드", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "제목은 필수입니다.") String title,
        /*
        Jackson이 자동으로 Enum으로 변환
        - 일치하지 않으면 HttpMessageNotReadableException 400으로 떨어집니다.
         */
        @Schema(description = "경험 유형", example = "PROJECT", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull(message = "경험 유형은 필수입니다.") ExperienceType experienceType,
        @Schema(description = "경험 상세 내용", example = "Spring Boot 기반 REST API를 설계하고 5명 규모 팀의 백엔드를 리드했습니다.", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotBlank(message = "경험 내용은 필수입니다.") String experienceContent,
        @Schema(description = "시작일 (오늘 이전 또는 오늘)", example = "2025-03-01")
        @PastOrPresent(message = "시작일은 오늘 이전(또는 오늘)이어야 합니다.") LocalDate startDate,
        @Schema(description = "종료일 (시작일 이후, 진행 중이면 생략 가능)", example = "2025-06-30")
        LocalDate endDate
) {
}
