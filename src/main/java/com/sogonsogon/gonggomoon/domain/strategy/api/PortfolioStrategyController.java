package com.sogonsogon.gonggomoon.domain.strategy.api;

import com.sogonsogon.gonggomoon.domain.auth.infrastructure.security.AccessUser;
import com.sogonsogon.gonggomoon.domain.strategy.api.request.GeneratePortfolioStrategyRequest;
import com.sogonsogon.gonggomoon.domain.strategy.api.response.GeneratePortfolioStrategyResponse;
import com.sogonsogon.gonggomoon.domain.strategy.api.response.PortfolioStrategyDetailResponse;
import com.sogonsogon.gonggomoon.domain.strategy.api.response.PortfolioStrategyListResponse;
import com.sogonsogon.gonggomoon.domain.strategy.application.PortfolioStrategyService;
import com.sogonsogon.gonggomoon.domain.strategy.application.result.GeneratePortfolioStrategyResult;
import com.sogonsogon.gonggomoon.domain.strategy.application.result.PortfolioStrategyDetailResult;
import com.sogonsogon.gonggomoon.domain.strategy.application.result.PortfolioStrategyListResult;
import com.sogonsogon.gonggomoon.global.response.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/portfolio-strategies")
@RequiredArgsConstructor
public class PortfolioStrategyController {
    private final PortfolioStrategyService portfolioStrategyService;

    /**
     * 포트폴리오 전략을 생생합니다.
     */
    @PostMapping
    public ResponseEntity<BaseResponse<GeneratePortfolioStrategyResponse>> generate(
            @AuthenticationPrincipal AccessUser user,
            @RequestBody @Valid GeneratePortfolioStrategyRequest req
    ) {
        GeneratePortfolioStrategyResult result = portfolioStrategyService.generate(user.getId(), req);

        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.success(GeneratePortfolioStrategyResponse.from(result)));
    }

    /**
     * 포트폴리오 전략 목록을 조회합니다.
     */
    @GetMapping
    public ResponseEntity<BaseResponse<PortfolioStrategyListResponse>> getPortfolioStrategyList(
            @AuthenticationPrincipal AccessUser user
    ) {
        PortfolioStrategyListResult result = portfolioStrategyService.getPortfolioStrategyList(user.getId());

        return ResponseEntity.ok(BaseResponse.success(PortfolioStrategyListResponse.from(result)));
    }

    /**
     * 포트폴리오 전략 상세를 조회합니다.
     */
    @GetMapping("/{strategyId}")
    public ResponseEntity<BaseResponse<PortfolioStrategyDetailResponse>> getPortfolioStrategyDetail(
            @AuthenticationPrincipal AccessUser user,
            @PathVariable("strategyId") Long strategyId) {
        PortfolioStrategyDetailResult result = portfolioStrategyService.getPortfolioStrategyDetail(strategyId, user.getId());

        return ResponseEntity.ok(BaseResponse.success(PortfolioStrategyDetailResponse.from(result)));
    }
}
