package com.sogonsogon.gonggomoon.domain.post.api;

import com.sogonsogon.gonggomoon.domain.post.application.PlatformService;
import com.sogonsogon.gonggomoon.domain.post.dto.response.PlatformListResponse;
import com.sogonsogon.gonggomoon.global.response.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/platforms")
public class PlatformController {

    private final PlatformService platformService;

    public PlatformController(PlatformService platformService) {
        this.platformService = platformService;
    }

    @GetMapping
    public ResponseEntity<BaseResponse<PlatformListResponse>> getPlatforms() {

        return ResponseEntity.ok(BaseResponse.success(platformService.getPlatformAll()));
    }
}
