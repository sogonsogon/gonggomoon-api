package com.sogonsogon.gonggomoon.domain.experience.api.request;

import com.sogonsogon.gonggomoon.domain.experience.domain.ExperienceType;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

public record CreateExperienceRequest(
        @NotBlank(message = "제목은 필수입니다.") String title,
        /*
        Jackson이 자동으로 Enum으로 변환
        - 일치하지 않으면 HttpMessageNotReadableException 400으로 떨어집니다.
         */
        @NotBlank(message = "경험 유형은 필수입니다.") ExperienceType experienceType,
        @NotBlank(message = "경험 내용은 필수입니다.") String experienceContent,
        @PastOrPresent(message = "시작일은 오늘 이전(또는 오늘)이어야 합니다.") LocalDate startDate,
        @FutureOrPresent(message = "종료일은 오늘 이후(또는 오늘)이어야 합니다.") LocalDate endDate
) {
}
