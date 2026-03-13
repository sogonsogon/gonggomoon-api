package com.sogonsogon.gonggomoon.domain.industry.application;

import com.sogonsogon.gonggomoon.domain.industry.domain.Industry;
import com.sogonsogon.gonggomoon.domain.industry.domain.IndustryRepository;
import com.sogonsogon.gonggomoon.domain.industry.dto.response.IndustryListResponse;
import com.sogonsogon.gonggomoon.domain.industry.dto.response.IndustryResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IndustryService {

    private final IndustryRepository industryRepository;

    public IndustryService(IndustryRepository industryRepository) {
        this.industryRepository = industryRepository;
    }

    public IndustryListResponse getIndustries() {

        List<Industry> industries = industryRepository.findAll();

        List<IndustryResponse> responses = industries.stream()
                .map(IndustryResponse::from)
                .toList();

        return IndustryListResponse.from(responses);
    }
}
