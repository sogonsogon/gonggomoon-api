package com.sogonsogon.gonggomoon.domain.post.api;

import com.sogonsogon.gonggomoon.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Hidden
@Deprecated
@Tag(name = "플랫폼", description = "플랫폼 조회 관련 API")
@RestController
@RequestMapping("/api/v1/platforms")
public class PlatformController {

//    private final PlatformService platformService;
//
//    public PlatformController(PlatformService platformService) {
//        this.platformService = platformService;
//    }
//
//    @Operation(summary = "플랫폼 목록 조회", description = "등록된 전체 플랫폼 목록을 조회합니다.")
//    @GetMapping
//    public ResponseEntity<BaseResponse<PlatformListResponse>> getPlatforms() {
//
//        return ResponseEntity.ok(BaseResponse.success(platformService.getPlatformAll()));
//    }
}
