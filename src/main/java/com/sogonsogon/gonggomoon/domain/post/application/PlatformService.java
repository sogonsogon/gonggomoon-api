package com.sogonsogon.gonggomoon.domain.post.application;

import com.sogonsogon.gonggomoon.domain.post.domain.Platform;
import com.sogonsogon.gonggomoon.domain.post.domain.PlatformRepository;
import com.sogonsogon.gonggomoon.domain.post.dto.response.PlatformListResponse;
import com.sogonsogon.gonggomoon.domain.post.dto.response.PlatformResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlatformService {

    private final PlatformRepository platformRepository;

    public PlatformService(PlatformRepository platformRepository) {
        this.platformRepository = platformRepository;
    }

    public PlatformListResponse getPlatformAll() {

        List<Platform> platforms = platformRepository.findAll();

        List<PlatformResponse> responses = platforms.stream()
                .map(PlatformResponse::from)
                .toList();

        return PlatformListResponse.from(responses);
    }
}
