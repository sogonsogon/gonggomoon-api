package com.sogonsogon.gonggomoon.domain.industry.application;

import com.sogonsogon.gonggomoon.domain.industry.domain.Industry;
import com.sogonsogon.gonggomoon.domain.industry.domain.IndustryReport;
import com.sogonsogon.gonggomoon.domain.industry.domain.IndustryReportRepository;
import com.sogonsogon.gonggomoon.domain.industry.domain.IndustryReportStatus;
import com.sogonsogon.gonggomoon.domain.industry.domain.IndustryRepository;
import com.sogonsogon.gonggomoon.domain.industry.dto.response.IndustryReportResponse;
import com.sogonsogon.gonggomoon.domain.industry.error.IndustryErrorCode;
import com.sogonsogon.gonggomoon.domain.industry.error.IndustryReportErrorCode;
import com.sogonsogon.gonggomoon.global.error.BaseException;
import org.springframework.stereotype.Service;

@Service
public class IndustryReportService {

    private final IndustryReportRepository industryReportRepository;
    private final IndustryRepository industryRepository;

    public IndustryReportService(IndustryReportRepository industryReportRepository, IndustryRepository industryRepository) {
        this.industryReportRepository = industryReportRepository;
        this.industryRepository = industryRepository;
    }

    public IndustryReportResponse getIndustryReport(Long id) {

        IndustryReport report = industryReportRepository.findById(id)
                .orElseThrow(() -> new BaseException(IndustryReportErrorCode.INDUSTRY_REPORT_NOT_FOUND));

        if (report.getStatus() != IndustryReportStatus.PUBLISHED) throw new BaseException(IndustryReportErrorCode.INDUSTRY_REPORT_NOT_PUBLISHED);

        Industry industry = industryRepository.findById(report.getIndustryId())
                .orElseThrow(() -> new BaseException(IndustryErrorCode.INDUSTRY_NOT_FOUND));


        return new IndustryReportResponse(
                report.getId(),
                industry.getName(),
                report.getStatus(),
                report.getReportYear(),
                report.getMarketSize(),
                report.getCompetition(),
                report.getTrend(),
                report.getRegulation(),
                report.getKeyword(),
                report.getHiring(),
                report.getInvestment(),
                report.getCreatedAt(),
                report.getUpdatedAt()
        );
    }
}
