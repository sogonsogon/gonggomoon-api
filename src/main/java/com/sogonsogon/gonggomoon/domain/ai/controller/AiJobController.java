package com.sogonsogon.gonggomoon.domain.ai.controller;

import com.sogonsogon.gonggomoon.domain.ai.application.AiService;
import com.sogonsogon.gonggomoon.domain.ai.domain.AiFunctions;
import com.sogonsogon.gonggomoon.domain.ai.dto.request.AiFunctionStatusRequest;
import com.sogonsogon.gonggomoon.domain.ai.dto.response.AiFunctionStatusResponse;
import com.sogonsogon.gonggomoon.domain.auth.infrastructure.security.AccessUser;
import com.sogonsogon.gonggomoon.global.response.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

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

    // 2. SSE 추가 (상태 실시간 구독용)
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subscribeAiJobStatus(
        @AuthenticationPrincipal AccessUser user,
        @RequestParam AiFunctions type,
        @RequestParam Long id
    ) {
        AiFunctionStatusRequest request = new AiFunctionStatusRequest(type, id);

        if (aiService.isTerminalJobStatus(user.getId(), request)) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(aiService.subscribe(user.getId(), request));
    }
}
