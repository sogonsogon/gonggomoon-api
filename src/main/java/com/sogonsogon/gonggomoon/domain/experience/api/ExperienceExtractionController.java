package com.sogonsogon.gonggomoon.domain.experience.api;

import com.sogonsogon.gonggomoon.domain.auth.infrastructure.security.AccessUser;
import com.sogonsogon.gonggomoon.domain.experience.api.request.ExperienceExtractRequest;
import com.sogonsogon.gonggomoon.domain.experience.api.response.ExperienceExtractionResponse;
import com.sogonsogon.gonggomoon.domain.experience.application.ExperienceExtractionService;
import com.sogonsogon.gonggomoon.domain.experience.application.result.ExperienceExtractionResult;
import com.sogonsogon.gonggomoon.domain.experience.application.result.ExperienceExtractionSearchResult;
import com.sogonsogon.gonggomoon.global.response.BaseResponse;
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
public class ExperienceExtractionController {

    private final ExperienceExtractionService extractionService;

    @PostMapping("/extractions")
    public ResponseEntity<BaseResponse<ExperienceExtractionResponse>> startExperienceExtraction(
            @AuthenticationPrincipal AccessUser user,
            @RequestBody @Valid ExperienceExtractRequest req) {
        ExperienceExtractionResult result = extractionService.startExperienceExtraction(req, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.success(ExperienceExtractionResponse.from(result)));
    }

    /*
    * ExperienceExtraction 단일 조회 API
    * */
    @GetMapping("/extractions/{extractionId}")
    public ResponseEntity<BaseResponse<ExperienceExtractionSearchResult>> getExperienceExtraction(
        @AuthenticationPrincipal AccessUser user,
        @PathVariable Long extractionId
    ) {
        ExperienceExtractionSearchResult response = extractionService.getExperienceExtraction(extractionId, user.getId());

        return ResponseEntity.ok(BaseResponse.success(response));
    }
}
