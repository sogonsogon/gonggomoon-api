package com.sogonsogon.gonggomoon.domain.ai.controller;

import com.sogonsogon.gonggomoon.domain.ai.application.AiService;
import com.sogonsogon.gonggomoon.domain.ai.domain.AiFunctions;
import com.sogonsogon.gonggomoon.domain.ai.dto.request.AiFunctionStatusRequest;
import com.sogonsogon.gonggomoon.domain.ai.dto.response.AiFunctionStatusResponse;
import com.sogonsogon.gonggomoon.domain.auth.infrastructure.security.AccessUser;
import com.sogonsogon.gonggomoon.global.response.BaseResponse;
import com.sogonsogon.gonggomoon.global.docs.ErrorResponseExamples;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import java.util.UUID;

@Tag(name = "AI 작업", description = "AI 기능 작업 상태 조회 및 실시간 구독 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ai-jobs")
public class AiJobController {

    private final AiService aiService;

    @Operation(summary = "AI 작업 상태 조회", description = "요청한 AI 기능 타입과 ID에 해당하는 작업의 현재 상태를 조회한다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "AI 작업 상태 조회 성공"),
            @ApiResponse(responseCode = "400",
                    description = "입력값 검증 실패(GLOBAL_INVALID_INPUT_VALUE) / 유효하지 않은 type(AI_INVALID_TYPE)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = ErrorResponseExamples.AI_INVALID_TYPE))),
            @ApiResponse(responseCode = "401", description = "인증 실패 (액세스 토큰 누락/만료)"),
            @ApiResponse(responseCode = "404",
                    description = "해당 작업을 찾을 수 없음(EXTRACTED_EXPERIENCE_NOT_FOUND)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = ErrorResponseExamples.EXTRACTED_EXPERIENCE_NOT_FOUND)))
    })
    @PostMapping("/status")
    public ResponseEntity<BaseResponse<AiFunctionStatusResponse>> checkAiJobStatus(
        @AuthenticationPrincipal AccessUser user,
        @RequestBody @Valid AiFunctionStatusRequest request
        ) {

        AiFunctionStatusResponse response = aiService.checkAiFunctionStatus(user.getId(), request);

        return ResponseEntity.ok(BaseResponse.success(response));
    }

    // 2. SSE 추가 (상태 실시간 구독용)
    @Operation(
            summary = "AI 작업 상태 실시간 구독",
            description = "AI 기능 타입과 ID로 작업 상태를 SSE로 실시간 구독한다. 이미 종료된 작업이면 현재 상태를 한 번 전송한 뒤 연결을 종료한다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "SSE 구독 시작 (text/event-stream) - 작업 상태 이벤트 수신"),
            @ApiResponse(responseCode = "400",
                    description = "유효하지 않은 type(AI_INVALID_TYPE)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = ErrorResponseExamples.AI_INVALID_TYPE))),
            @ApiResponse(responseCode = "401", description = "인증 실패 (액세스 토큰 누락/만료)"),
            @ApiResponse(responseCode = "404",
                    description = "해당 작업을 찾을 수 없음(EXTRACTED_EXPERIENCE_NOT_FOUND)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = ErrorResponseExamples.EXTRACTED_EXPERIENCE_NOT_FOUND)))
    })
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subscribeAiJobStatus(
        @AuthenticationPrincipal AccessUser user,
        @RequestParam AiFunctions type,
        @RequestParam UUID id
    ) {
        AiFunctionStatusRequest request = new AiFunctionStatusRequest(type, id);

        return ResponseEntity.ok(aiService.subscribe(user.getId(), request));
    }
}
