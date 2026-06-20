package com.sogonsogon.gonggomoon.domain.experience.api;

import com.sogonsogon.gonggomoon.domain.auth.infrastructure.security.AccessUser;
import com.sogonsogon.gonggomoon.domain.experience.api.request.ExperienceExtractRequest;
import com.sogonsogon.gonggomoon.domain.experience.api.response.ExperienceExtractionResponse;
import com.sogonsogon.gonggomoon.domain.experience.application.ExperienceExtractionAvailabilityService;
import com.sogonsogon.gonggomoon.domain.experience.application.ExperienceExtractionService;
import com.sogonsogon.gonggomoon.domain.experience.application.result.ExperienceExtractionAvailabilityResult;
import com.sogonsogon.gonggomoon.domain.experience.application.result.ExperienceExtractionResult;
import com.sogonsogon.gonggomoon.domain.experience.application.result.ExperienceExtractionSearchResult;
import com.sogonsogon.gonggomoon.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/experiences")
@RequiredArgsConstructor
@Tag(name = "경험 추출", description = "AI를 활용하여 사용자의 경험을 추출하고 그 결과를 조회하는 API")
public class ExperienceExtractionController {

    private final ExperienceExtractionService extractionService;
    private final ExperienceExtractionAvailabilityService extractionAvailabilityService;

    @Operation(summary = "경험 추출 시작", description = "요청한 입력 데이터를 기반으로 AI 경험 추출 작업을 시작합니다.")
    @PostMapping("/extractions")
    public ResponseEntity<BaseResponse<ExperienceExtractionResponse>> startExperienceExtraction(
            @AuthenticationPrincipal AccessUser user,
            @RequestBody @Valid ExperienceExtractRequest req) {
        ExperienceExtractionResult result = extractionService.startExperienceExtraction(req, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.success(ExperienceExtractionResponse.from(result)));
    }

    @Operation(summary = "경험 추출 가능 여부 조회", description = "로그인한 사용자가 경험 추출을 수행할 수 있는지 가능 여부를 조회합니다.")
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
    @GetMapping("/extractions/{extractionId}")
    public ResponseEntity<BaseResponse<ExperienceExtractionSearchResult>> getExperienceExtraction(
        @AuthenticationPrincipal AccessUser user,
        @PathVariable Long extractionId
    ) {
        ExperienceExtractionSearchResult response = extractionService.getExperienceExtraction(extractionId, user.getId());

        return ResponseEntity.ok(BaseResponse.success(response));
    }
}
