package com.sogonsogon.gonggomoon.domain.post.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record TavilyExtractResponse(
        List<Result> results
) {
    public record Result(
            String url,
            @JsonProperty("raw_content") String rawContent
    ) {}
}
