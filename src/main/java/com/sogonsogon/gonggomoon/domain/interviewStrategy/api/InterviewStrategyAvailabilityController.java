package com.sogonsogon.gonggomoon.domain.interviewStrategy.api;

import com.sogonsogon.gonggomoon.domain.auth.infrastructure.security.AccessUser;
import com.sogonsogon.gonggomoon.domain.interviewStrategy.api.response.InterviewStrategyAvailabilityResponse;
import com.sogonsogon.gonggomoon.domain.interviewStrategy.application.InterviewStrategyAvailabilityService;
import com.sogonsogon.gonggomoon.domain.interviewStrategy.application.result.InterviewStrategyAvailabilityResult;
import com.sogonsogon.gonggomoon.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "면접 전략 생성 가능 여부", description = "면접 전략 질문 세트 생성 가능 여부 조회 API")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class InterviewStrategyAvailabilityController {

    private final InterviewStrategyAvailabilityService interviewStrategyAvailabilityService;

    /**
     * 생성 가능한지 조회합니다.
     */
    @Operation(summary = "면접 전략 생성 가능 여부 조회", description = "로그인한 사용자가 면접 전략 질문 세트를 생성할 수 있는지 여부를 조회합니다.")
    @GetMapping("/interview-strategies/availability")
    public ResponseEntity<BaseResponse<InterviewStrategyAvailabilityResponse>> getStrategyAvailability(
            @AuthenticationPrincipal AccessUser user
    ) {
        InterviewStrategyAvailabilityResult result = interviewStrategyAvailabilityService.getAvailability(user.getId());

        return ResponseEntity.ok(BaseResponse.success(InterviewStrategyAvailabilityResponse.from(result)));
    }
}
