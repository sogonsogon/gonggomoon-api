package com.sogonsogon.gonggomoon.domain.ai.controller;

import com.sogonsogon.gonggomoon.domain.ai.application.AiCallbackService;
import com.sogonsogon.gonggomoon.domain.ai.dto.request.BaseCallbackRequest;
import com.sogonsogon.gonggomoon.global.response.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/callbacks")
@RequiredArgsConstructor
public class AiCallbackController {

    private final AiCallbackService aiCallbackService;

    @PostMapping("/experience-extraction")
    public ResponseEntity<BaseResponse<String>> handleExperienceExtractionCallback(
        @RequestBody @Valid BaseCallbackRequest request
    ) {
        aiCallbackService.createExtractedExperience(request);

        return ResponseEntity.ok(BaseResponse.success("AI 경험 추출 콜백 처리 완료"));
    }

    @PostMapping("/portfolio-strategy-generation")
    public ResponseEntity<BaseResponse<String>> handlePortfolioGenerationCallback(
        @RequestBody @Valid BaseCallbackRequest request
    ) {
        aiCallbackService.updatePortfolioStrategy(request);

        return ResponseEntity.ok(BaseResponse.success("AI 포폴 전략 생성 콜백 처리 완료"));
    }

}
