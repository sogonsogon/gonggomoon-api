package com.sogonsogon.gonggomoon.domain.industry.api;

import com.sogonsogon.gonggomoon.domain.industry.application.IndustryReportService;
import com.sogonsogon.gonggomoon.domain.industry.dto.response.IndustryReportResponse;
import com.sogonsogon.gonggomoon.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Hidden
@Deprecated
@RestController
@RequestMapping("/api/v1/industries")
@Tag(name = "산업 리포트", description = "산업별 리포트를 조회하는 API")
public class IndustryReportController {

    private final IndustryReportService industryReportService;

    public IndustryReportController(IndustryReportService industryReportService) {
        this.industryReportService = industryReportService;
    }

    @Operation(summary = "산업 리포트 조회", description = "산업 ID에 해당하는 산업의 리포트를 조회합니다.")
    @GetMapping("/{id}/reports")
    public ResponseEntity<BaseResponse<IndustryReportResponse>> getIndustryReport(@PathVariable Long id) {

        return ResponseEntity.ok(BaseResponse.success(industryReportService.getIndustryReport(id)));
    }
}
