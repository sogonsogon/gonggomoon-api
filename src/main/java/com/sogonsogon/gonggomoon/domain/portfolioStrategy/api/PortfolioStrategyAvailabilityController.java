package com.sogonsogon.gonggomoon.domain.portfolioStrategy.api;

import com.sogonsogon.gonggomoon.domain.auth.infrastructure.security.AccessUser;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.api.response.PortfolioStrategyAvailabilityResponse;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.application.PortfolioStrategyAvailabilityService;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.application.result.PortfolioStrategyAvailabilityResult;
import com.sogonsogon.gonggomoon.global.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PortfolioStrategyAvailabilityController {

    private final PortfolioStrategyAvailabilityService portfolioStrategyAvailabilityService;

    /**
     * 생성 가능한지 조회합니다.
     */
    @GetMapping("/strategies/availability")
    public ResponseEntity<BaseResponse<PortfolioStrategyAvailabilityResponse>> getStrategyAvailability(
            @AuthenticationPrincipal AccessUser user
    ) {
        PortfolioStrategyAvailabilityResult result = portfolioStrategyAvailabilityService.getAvailability(user.getId());

        return ResponseEntity.ok(BaseResponse.success(PortfolioStrategyAvailabilityResponse.from(result)));
    }
}
