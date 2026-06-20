package com.sogonsogon.gonggomoon.domain.industry.api;

import com.sogonsogon.gonggomoon.domain.industry.application.IndustryService;
import com.sogonsogon.gonggomoon.domain.industry.dto.response.IndustryListResponse;
import com.sogonsogon.gonggomoon.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/industries")
@Tag(name = "산업", description = "산업 정보를 조회하는 API")
public class IndustryController {

    private final IndustryService industryService;

    public IndustryController(IndustryService industryService) {
        this.industryService = industryService;
    }

    @Operation(summary = "산업 목록 조회", description = "등록된 전체 산업 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<BaseResponse<IndustryListResponse>> getIndustries() {

        return ResponseEntity.ok(BaseResponse.success(industryService.getIndustries()));
    }
}
