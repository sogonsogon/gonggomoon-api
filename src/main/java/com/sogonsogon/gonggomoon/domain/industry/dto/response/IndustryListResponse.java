package com.sogonsogon.gonggomoon.domain.industry.dto.response;

import java.util.List;

public record IndustryListResponse(
        List<IndustryResponse> content
) {
    public static IndustryListResponse from(List<IndustryResponse> responses) {
        return new IndustryListResponse(responses);
    }
}
