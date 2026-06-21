package com.sogonsogon.gonggomoon.domain.portfolioStrategy.api;

import com.sogonsogon.gonggomoon.domain.auth.infrastructure.security.AccessUser;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.api.response.PortfolioStrategyAvailabilityResponse;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.application.PortfolioStrategyAvailabilityService;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.application.result.PortfolioStrategyAvailabilityResult;
import com.sogonsogon.gonggomoon.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "포트폴리오 전략 생성 가능 여부", description = "포트폴리오 전략 생성 가능 여부 조회 API")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PortfolioStrategyAvailabilityController {

    private final PortfolioStrategyAvailabilityService portfolioStrategyAvailabilityService;

    /**
     * 생성 가능한지 조회합니다.
     */
    @Operation(summary = "포트폴리오 전략 생성 가능 여부 조회", description = "로그인한 사용자가 포트폴리오 전략을 생성할 수 있는지 여부를 조회합니다.")
    @GetMapping("/strategies/availability")
    public ResponseEntity<BaseResponse<PortfolioStrategyAvailabilityResponse>> getStrategyAvailability(
            @AuthenticationPrincipal AccessUser user
    ) {
        PortfolioStrategyAvailabilityResult result = portfolioStrategyAvailabilityService.getAvailability(user.getId());

        return ResponseEntity.ok(BaseResponse.success(PortfolioStrategyAvailabilityResponse.from(result)));
    }
}
