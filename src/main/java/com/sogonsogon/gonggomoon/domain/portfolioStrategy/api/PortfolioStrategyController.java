package com.sogonsogon.gonggomoon.domain.portfolioStrategy.api;

import com.sogonsogon.gonggomoon.domain.auth.infrastructure.security.AccessUser;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.api.request.GeneratePortfolioStrategyRequest;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.api.response.GeneratePortfolioStrategyResponse;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.api.response.PortfolioStrategyDetailResponse;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.api.response.PortfolioStrategyListResponse;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.application.PortfolioStrategyService;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.application.result.GeneratePortfolioStrategyResult;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.application.result.PortfolioStrategyDetailResult;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.application.result.PortfolioStrategyListResult;
import com.sogonsogon.gonggomoon.global.docs.ErrorResponseExamples;
import com.sogonsogon.gonggomoon.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "포트폴리오 전략", description = "포트폴리오 전략 생성, 조회, 삭제 API")
@RestController
@RequestMapping("/api/v1/portfolio-strategies")
@RequiredArgsConstructor
public class PortfolioStrategyController {
    private final PortfolioStrategyService portfolioStrategyService;

    /**
     * 포트폴리오 전략을 생생합니다.
     */
    @Operation(summary = "포트폴리오 전략 생성", description = "로그인한 사용자의 요청 정보를 기반으로 포트폴리오 전략을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "포트폴리오 전략 생성 작업 시작 성공"),
            @ApiResponse(responseCode = "400",
                    description = "입력값 검증 실패(GLOBAL_INVALID_INPUT_VALUE) / 경험 선택 필수(PORTFOLIO_STRATEGY_EXPERIENCE_IDS_REQUIRED) / 요청한 경험을 찾을 수 없음(PORTFOLIO_STRATEGY_REQUESTED_EXPERIENCE_NOT_FOUND)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = ErrorResponseExamples.PORTFOLIO_STRATEGY_REQUESTED_EXPERIENCE_NOT_FOUND))),
            @ApiResponse(responseCode = "401", description = "인증 실패 (액세스 토큰 누락/만료)"),
            @ApiResponse(responseCode = "409",
                    description = "이번 주 포트폴리오 전략 생성 가능 횟수 초과(PORTFOLIO_STRATEGY_WEEKLY_LIMIT_EXCEEDED)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = ErrorResponseExamples.PORTFOLIO_STRATEGY_WEEKLY_LIMIT_EXCEEDED)))
    })
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
    @Operation(summary = "포트폴리오 전략 목록 조회", description = "로그인한 사용자가 생성한 포트폴리오 전략 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "포트폴리오 전략 목록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패 (액세스 토큰 누락/만료)")
    })
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
    @Operation(summary = "포트폴리오 전략 상세 조회", description = "전략 ID로 로그인한 사용자의 포트폴리오 전략 상세 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "포트폴리오 전략 상세 조회 성공"),
            @ApiResponse(responseCode = "202",
                    description = "전략 결과가 아직 준비되지 않음 - 생성 진행 중(PORTFOLIO_STRATEGY_RESULT_NOT_READY)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = ErrorResponseExamples.PORTFOLIO_STRATEGY_RESULT_NOT_READY))),
            @ApiResponse(responseCode = "401", description = "인증 실패 (액세스 토큰 누락/만료)"),
            @ApiResponse(responseCode = "404",
                    description = "전략을 찾을 수 없거나 본인 소유가 아님(PORTFOLIO_STRATEGY_NOT_FOUND) / 존재하지 않는 산업(INDUSTRY_NOT_FOUND)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = ErrorResponseExamples.PORTFOLIO_STRATEGY_NOT_FOUND))),
            @ApiResponse(responseCode = "500",
                    description = "전략 생성 실패(PORTFOLIO_STRATEGY_GENERATION_FAILED) / 결과 JSON 비어있음(PORTFOLIO_STRATEGY_RESULT_JSON_EMPTY) / 역직렬화 실패(PORTFOLIO_STRATEGY_RESULT_JSON_DESERIALIZATION_FAILED)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = ErrorResponseExamples.PORTFOLIO_STRATEGY_GENERATION_FAILED)))
    })
    @GetMapping("/{strategyId}")
    public ResponseEntity<BaseResponse<PortfolioStrategyDetailResponse>> getPortfolioStrategyDetail(
            @AuthenticationPrincipal AccessUser user,
            @PathVariable("strategyId") Long strategyId) {
        PortfolioStrategyDetailResult result = portfolioStrategyService.getPortfolioStrategyDetail(strategyId, user.getId());

        return ResponseEntity.ok(BaseResponse.success(PortfolioStrategyDetailResponse.from(result)));
    }

    /**
     * 포트폴리오 전략을 삭제합니다.
     */
    @Operation(summary = "포트폴리오 전략 삭제", description = "전략 ID로 로그인한 사용자의 포트폴리오 전략을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "포트폴리오 전략 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패 (액세스 토큰 누락/만료)"),
            @ApiResponse(responseCode = "404",
                    description = "전략을 찾을 수 없거나 본인 소유가 아님(PORTFOLIO_STRATEGY_NOT_FOUND)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = ErrorResponseExamples.PORTFOLIO_STRATEGY_NOT_FOUND)))
    })
    @DeleteMapping("/{strategyId}")
    public ResponseEntity<BaseResponse<Void>> deletePortfolioStrategy(
            @AuthenticationPrincipal AccessUser user,
            @PathVariable("strategyId") Long strategyId) {
        portfolioStrategyService.deletePortfolioStrategy(strategyId, user.getId());

        return ResponseEntity.ok(BaseResponse.success());
    }
}
