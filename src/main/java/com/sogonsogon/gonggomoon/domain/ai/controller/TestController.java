package com.sogonsogon.gonggomoon.domain.ai.controller;

import com.sogonsogon.gonggomoon.domain.ai.application.AiService;
import com.sogonsogon.gonggomoon.domain.ai.dto.request.ExperienceExtractRequest;
import com.sogonsogon.gonggomoon.domain.ai.dto.response.ExperienceExtractResponse;
import com.sogonsogon.gonggomoon.global.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/*
* 이게 경험 추출 요청 컨트롤러라고 가정을 하고 테스트를 위해 만든 컨트롤러입니다.
* */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/test")
public class TestController {

    private final AiService aiService;

    @PostMapping("/extract-experience")
    public ResponseEntity<BaseResponse<ExperienceExtractResponse>> createExtractExperienceJob(
        @RequestBody ExperienceExtractRequest request
        ) {
        ExperienceExtractResponse response = aiService.requestExperienceExtraction(
            request.userId(),
            request.fileAssetId());
        return ResponseEntity.ok(BaseResponse.success(response));
    }
}
