package com.sogonsogon.gonggomoon.domain.ai.dto.request;

/**
 * 공고 URL 분석 작업을 AI 워커로 전달하기 위한 메시지 바디.
 * id는 콜백의 최상위 id로 에코백되는 post ID다.
 */
public record PostAnalysisAiServerRequest(
        Long id,
        Long userId,
        String callbackUrl,
        Long postId,
        Long fileAssetId
) {
}
