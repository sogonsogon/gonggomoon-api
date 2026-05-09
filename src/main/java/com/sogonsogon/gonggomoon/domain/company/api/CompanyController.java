package com.sogonsogon.gonggomoon.domain.company.api;

import com.sogonsogon.gonggomoon.domain.company.application.CompanyService;
import com.sogonsogon.gonggomoon.domain.company.dto.response.CompanyResponse;
import com.sogonsogon.gonggomoon.global.response.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/companies")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<CompanyResponse>> getCompany(@PathVariable Long id) {

        return ResponseEntity.ok(BaseResponse.success(companyService.getCompany(id)));
    }
}
