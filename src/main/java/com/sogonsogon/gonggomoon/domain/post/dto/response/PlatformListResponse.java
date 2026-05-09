package com.sogonsogon.gonggomoon.domain.post.dto.response;

import java.util.List;

public record PlatformListResponse(
        List<PlatformResponse> content
) {
    public static PlatformListResponse from(List<PlatformResponse> platforms) {
        return new PlatformListResponse(platforms);
    }
}
