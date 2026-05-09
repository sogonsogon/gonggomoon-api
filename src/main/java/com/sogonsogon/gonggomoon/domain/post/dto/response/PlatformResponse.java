package com.sogonsogon.gonggomoon.domain.post.dto.response;

import com.sogonsogon.gonggomoon.domain.post.domain.Platform;

public record PlatformResponse(
        Long platformId,
        String platformName
) {
    public static PlatformResponse from(Platform platform) {
        return new PlatformResponse(
                platform.getId(),
                platform.getName()
        );
    }
}
