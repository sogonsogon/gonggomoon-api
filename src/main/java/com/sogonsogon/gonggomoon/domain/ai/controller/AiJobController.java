package com.sogonsogon.gonggomoon.domain.ai.controller;

import com.sogonsogon.gonggomoon.domain.ai.application.AiService;
import com.sogonsogon.gonggomoon.domain.ai.dto.request.AiFunctionStatusRequest;
import com.sogonsogon.gonggomoon.domain.ai.dto.response.AiFunctionStatusResponse;
import com.sogonsogon.gonggomoon.domain.auth.infrastructure.security.AccessUser;
import com.sogonsogon.gonggomoon.global.response.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ai-jobs")
public class AiJobController {

    private final AiService aiService;

    @PostMapping("/status")
    public ResponseEntity<BaseResponse<AiFunctionStatusResponse>> checkAiJobStatus(
        @AuthenticationPrincipal AccessUser user,
        @RequestBody @Valid AiFunctionStatusRequest request
        ) {

        AiFunctionStatusResponse response = aiService.checkAiFunctionStatus(user.getId(), request);

        return ResponseEntity.ok(BaseResponse.success(response));
    }
}
