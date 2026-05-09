package com.sogonsogon.gonggomoon.domain.industry.dto.response;

import com.sogonsogon.gonggomoon.domain.industry.domain.Industry;

public record IndustryResponse(
        Long industryId,
        String industryName
) {
    public static IndustryResponse from(Industry industry) {
        return new IndustryResponse(
                industry.getId(),
                industry.getName()
        );
    }
}
