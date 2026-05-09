package com.sogonsogon.gonggomoon.domain.post.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public record SubmitPostRequest(

        @NotBlank(message = "URL은 필수입니다.")
        @Pattern(regexp = "^https?://.+", message = "올바른 형식의 URL이 아닙니다.")
        String postUrl,

        @NotNull(message = "플랫폼 ID는 필수입니다.")
        @Positive(message = "올바른 플랫폼 ID가 아닙니다.")
        Long platformId
) {
}
