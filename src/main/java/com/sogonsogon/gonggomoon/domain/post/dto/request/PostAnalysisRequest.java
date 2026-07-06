package com.sogonsogon.gonggomoon.domain.post.dto.request;

import jakarta.validation.constraints.NotBlank;

public record PostAnalysisRequest(

        @NotBlank(message = "URL은 필수입니다.")
        String postUrl
) {
}
