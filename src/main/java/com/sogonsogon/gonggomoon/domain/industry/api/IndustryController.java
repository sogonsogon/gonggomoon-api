package com.sogonsogon.gonggomoon.domain.industry.api;

import com.sogonsogon.gonggomoon.domain.industry.application.IndustryService;
import com.sogonsogon.gonggomoon.domain.industry.dto.response.IndustryListResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/industries")
public class IndustryController {

    private final IndustryService industryService;

    public IndustryController(IndustryService industryService) {
        this.industryService = industryService;
    }

    @GetMapping
    public IndustryListResponse getIndustries() {

        return industryService.getIndustries();
    }
}
