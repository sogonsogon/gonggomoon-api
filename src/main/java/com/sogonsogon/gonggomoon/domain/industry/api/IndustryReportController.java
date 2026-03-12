package com.sogonsogon.gonggomoon.domain.industry.api;

import com.sogonsogon.gonggomoon.domain.industry.application.IndustryReportService;
import com.sogonsogon.gonggomoon.domain.industry.dto.response.IndustryReportResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/industries/reports")
public class IndustryReportController {

    private final IndustryReportService industryReportService;

    public IndustryReportController(IndustryReportService industryReportService) {
        this.industryReportService = industryReportService;
    }

    @GetMapping("/{id}")
    public IndustryReportResponse getIndustryReport(@PathVariable Long id) {

        return industryReportService.getIndustryReport(id);
    }
}
