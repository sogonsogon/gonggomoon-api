package com.sogonsogon.gonggomoon.domain.company.application;

import com.sogonsogon.gonggomoon.domain.company.domain.Company;
import com.sogonsogon.gonggomoon.domain.company.domain.CompanyRepository;
import com.sogonsogon.gonggomoon.domain.company.dto.response.CompanyResponse;
import com.sogonsogon.gonggomoon.domain.company.error.CompanyErrorCode;
import com.sogonsogon.gonggomoon.domain.industry.domain.Industry;
import com.sogonsogon.gonggomoon.domain.industry.domain.IndustryRepository;
import com.sogonsogon.gonggomoon.domain.industry.error.IndustryErrorCode;
import com.sogonsogon.gonggomoon.global.error.BaseException;
import org.springframework.stereotype.Service;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final IndustryRepository industryRepository;

    public CompanyService(CompanyRepository companyRepository, IndustryRepository industryRepository) {
        this.companyRepository = companyRepository;
        this.industryRepository = industryRepository;
    }

    //TODO 쿼리로 한번에 조회해야 하나? 에러처리 힘들 수 있음
    public CompanyResponse getCompany(Long id) {

        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new BaseException(CompanyErrorCode.COMPANY_NOT_FOUND));

        Industry industry = industryRepository.findById(company.getIndustryId())
                .orElseThrow(() -> new BaseException(IndustryErrorCode.INDUSTRY_NOT_FOUND));

        return new CompanyResponse(
                company.getId(),
                company.getIndustryId(),
                company.getName(),
                industry.getName(),
                company.getType(),
                company.getEmployeeCount(),
                company.getAddress(),
                company.getDescription(),
                company.getFoundedYear(),
                company.getUrl(),
                company.getCreatedAt(),
                company.getUpdatedAt()
        );
    }
}
