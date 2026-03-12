package com.sogonsogon.gonggomoon.domain.company.application;

import com.sogonsogon.gonggomoon.domain.company.domain.Company;
import com.sogonsogon.gonggomoon.domain.company.domain.CompanyRepository;
import com.sogonsogon.gonggomoon.domain.company.dto.response.CompanyResponse;
import com.sogonsogon.gonggomoon.domain.company.error.CompanyErrorCode;
import com.sogonsogon.gonggomoon.global.error.BaseException;
import org.springframework.stereotype.Service;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public CompanyResponse getCompany(Long id) {

        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new BaseException(CompanyErrorCode.COMPANY_NOT_FOUND));


        return new CompanyResponse(
                company.getId(),
                company.getIndustryId(),
                company.getName(),
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
