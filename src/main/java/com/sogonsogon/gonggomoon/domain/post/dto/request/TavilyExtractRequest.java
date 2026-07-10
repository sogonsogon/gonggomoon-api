package com.sogonsogon.gonggomoon.domain.post.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record TavilyExtractRequest(
        @JsonProperty("api_key") String apikey,
        List<String> urls,
        @JsonProperty("extract_depth") String extractDepth,
        @JsonProperty("include_images") boolean includeImages,
        String format
) {
}
