package com.sogonsogon.gonggomoon.domain.company.api;

import com.sogonsogon.gonggomoon.domain.company.application.CompanyService;
import com.sogonsogon.gonggomoon.domain.company.dto.response.CompanyResponse;
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
@Tag(name = "기업", description = "기업 정보 조회 API")
@RestController
@RequestMapping("/api/v1/companies")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @Operation(summary = "기업 단건 조회", description = "기업 ID로 해당 기업의 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<CompanyResponse>> getCompany(@PathVariable Long id) {

        return ResponseEntity.ok(BaseResponse.success(companyService.getCompany(id)));
    }
}
