package com.sogonsogon.gonggomoon.domain.ai.controller;

import com.sogonsogon.gonggomoon.domain.ai.application.AiCallbackService;
import com.sogonsogon.gonggomoon.domain.ai.dto.request.ExperienceExtractionCallbackRequest;
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

    @PostMapping("/experience_extraction")
    public ResponseEntity<BaseResponse<String>> handleExperienceExtractionCallback(
        @RequestBody @Valid ExperienceExtractionCallbackRequest request
    ) {
        aiCallbackService.createExtractedExperience(request);

        return ResponseEntity.ok(BaseResponse.success("AI 경험 추출 콜백 처리 완료"));
    }

}
