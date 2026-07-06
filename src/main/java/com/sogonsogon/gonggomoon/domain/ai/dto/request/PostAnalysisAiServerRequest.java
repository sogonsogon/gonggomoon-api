package com.sogonsogon.gonggomoon.domain.ai.dto.request;

public record PostAnalysisAiServerRequest(
        Long userId,
        String callbackUrl,
        Long postId,
        Long fileAssetId
) {
}
