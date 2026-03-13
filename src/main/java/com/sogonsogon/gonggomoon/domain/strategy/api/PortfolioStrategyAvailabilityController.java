package com.sogonsogon.gonggomoon.domain.strategy.api;

import com.sogonsogon.gonggomoon.domain.auth.infrastructure.security.AccessUser;
import com.sogonsogon.gonggomoon.domain.strategy.api.response.StrategyAvailabilityResponse;
import com.sogonsogon.gonggomoon.domain.strategy.application.PortfolioStrategyAvailabilityService;
import com.sogonsogon.gonggomoon.domain.strategy.application.result.StrategyAvailabilityResult;
import com.sogonsogon.gonggomoon.global.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PortfolioStrategyAvailabilityController {

    private final PortfolioStrategyAvailabilityService strategyAvailabilityService;

    /**
     * 생성 가능한지 조회합니다.
     */
    @GetMapping("/api/v1/strategies/availability")
    public ResponseEntity<BaseResponse<StrategyAvailabilityResponse>> getStrategyAvailability(
            @AuthenticationPrincipal AccessUser user
    ) {
        StrategyAvailabilityResult result = strategyAvailabilityService.getAvailability(user.getId());

        return ResponseEntity.ok(BaseResponse.success(StrategyAvailabilityResponse.from(result)));
    }
}
