package com.sogonsogon.gonggomoon.domain.industry.dto.response;

import com.sogonsogon.gonggomoon.domain.industry.domain.IndustryReportStatus;

import java.time.Instant;
import java.util.List;

public record IndustryReportResponse(
        Long industryReportId,
        String industryName,
        IndustryReportStatus reportStatus,
        Integer reportYear,
        String marketSize,
        String competition,
        List<String> trend,
        List<String> regulation,
        List<String> keyword,
        List<String> hiring,
        List<String> investment,
        Instant createdAt,
        Instant updatedAt
        ) {
}
