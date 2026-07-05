package com.sogonsogon.gonggomoon.domain.experience.api;

import com.sogonsogon.gonggomoon.domain.auth.infrastructure.security.AccessUser;
import com.sogonsogon.gonggomoon.domain.experience.api.response.ExperienceExtractionResponse;
import com.sogonsogon.gonggomoon.domain.experience.application.ExperienceExtractionAvailabilityService;
import com.sogonsogon.gonggomoon.domain.experience.application.ExperienceExtractionService;
import com.sogonsogon.gonggomoon.domain.experience.application.result.ExperienceExtractionAvailabilityResult;
import com.sogonsogon.gonggomoon.domain.experience.application.result.ExperienceExtractionResult;
import com.sogonsogon.gonggomoon.domain.experience.application.result.ExperienceExtractionSearchResult;
import com.sogonsogon.gonggomoon.domain.file.api.request.UploadFileRequest;
import com.sogonsogon.gonggomoon.domain.file.domain.DocumentCategory;
import com.sogonsogon.gonggomoon.global.docs.ErrorResponseExamples;
import com.sogonsogon.gonggomoon.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/experiences")
@RequiredArgsConstructor
@Tag(name = "경험 추출", description = "AI를 활용하여 사용자의 경험을 추출하고 그 결과를 조회하는 API")
public class ExperienceExtractionController {

    private final ExperienceExtractionService extractionService;
    private final ExperienceExtractionAvailabilityService extractionAvailabilityService;

    @Operation(summary = "경험 추출 시작", description = "업로드한 파일을 기반으로 AI 경험 추출 작업을 시작합니다. 파일은 추출 처리 동안만 임시 저장되며 처리 완료 후 삭제됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "경험 추출 작업 시작 성공"),
            @ApiResponse(responseCode = "400",
                    description = "입력값 검증 실패(GLOBAL_INVALID_INPUT_VALUE) / 파일 누락 또는 비어있음(FILE_REQUIRED, EMPTY_FILE_NOT_ALLOWED) / 파일 용량 초과(FILE_SIZE_EXCEEDED)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = ErrorResponseExamples.EXPERIENCE_INVALID_FILE_ASSET_REQUEST))),
            @ApiResponse(responseCode = "401", description = "인증 실패 (액세스 토큰 누락/만료)"),
            @ApiResponse(responseCode = "409",
                    description = "이번 주 경험 추출 가능 횟수 초과(EXPERIENCE_WEEKLY_LIMIT_EXCEEDED)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = ErrorResponseExamples.EXPERIENCE_WEEKLY_LIMIT_EXCEEDED))),
            @ApiResponse(responseCode = "500",
                    description = "AI 서버 오류(AI_SERVER_ERROR)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = ErrorResponseExamples.AI_SERVER_ERROR)))
    })
    @PostMapping(value = "/extractions", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse<ExperienceExtractionResponse>> startExperienceExtraction(
            @AuthenticationPrincipal AccessUser user,
//            @RequestPart("request") @Valid UploadFileRequest req,
            @RequestPart("file") MultipartFile file) {
        UploadFileRequest req = new UploadFileRequest(DocumentCategory.RESUME);
        ExperienceExtractionResult result = extractionService.startExperienceExtraction(req, file, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.success(ExperienceExtractionResponse.from(result)));
    }

    @Operation(summary = "경험 추출 가능 여부 조회", description = "로그인한 사용자가 경험 추출을 수행할 수 있는지 가능 여부를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "경험 추출 가능 여부 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패 (액세스 토큰 누락/만료)")
    })
    @GetMapping("/extractions/availability")
    public ResponseEntity<BaseResponse<ExperienceExtractionAvailabilityResult>> getExtractionAvailability(
            @AuthenticationPrincipal AccessUser user
    ) {
        ExperienceExtractionAvailabilityResult result =
                extractionAvailabilityService.getAvailability(user.getId());

        return ResponseEntity.ok(BaseResponse.success(result));
    }

    /*
    * ExperienceExtraction 단일 조회 API
    * */
    @Operation(summary = "경험 추출 단일 조회", description = "추출 ID에 해당하는 경험 추출 작업의 결과를 단건 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "경험 추출 결과 단건 조회 성공"),
            @ApiResponse(responseCode = "400",
                    description = "추출된 경험이 비어 있음(EXTRACTED_EXPERIENCE_IS_EMPTY)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = ErrorResponseExamples.EXTRACTED_EXPERIENCE_IS_EMPTY))),
            @ApiResponse(responseCode = "401", description = "인증 실패 (액세스 토큰 누락/만료)"),
            @ApiResponse(responseCode = "404",
                    description = "추출 작업을 찾을 수 없거나 본인 소유가 아님(EXTRACTED_EXPERIENCE_NOT_FOUND)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = ErrorResponseExamples.EXTRACTED_EXPERIENCE_NOT_FOUND)))
    })
    @GetMapping("/extractions/{extractionId}")
    public ResponseEntity<BaseResponse<ExperienceExtractionSearchResult>> getExperienceExtraction(
        @AuthenticationPrincipal AccessUser user,
        @PathVariable Long extractionId
    ) {
        ExperienceExtractionSearchResult response = extractionService.getExperienceExtraction(extractionId, user.getId());

        return ResponseEntity.ok(BaseResponse.success(response));
    }
}
