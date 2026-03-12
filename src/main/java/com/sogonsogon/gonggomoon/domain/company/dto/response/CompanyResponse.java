package com.sogonsogon.gonggomoon.domain.company.dto.response;

import com.sogonsogon.gonggomoon.domain.company.domain.CompanyType;

import java.time.Instant;

public record CompanyResponse(
        Long companyId,
        Long industryId,
        String companyName,
        CompanyType companyType,
        Integer employeeCount,
        String address,
        String description,
        Integer foundedYear,
        String companyUrl,
        Instant createdAt,
        Instant updatedAt
) {
}
