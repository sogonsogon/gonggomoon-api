package com.sogonsogon.gonggomoon.domain.interviewStrategy.api;

import com.sogonsogon.gonggomoon.domain.auth.infrastructure.security.AccessUser;
import com.sogonsogon.gonggomoon.domain.interviewStrategy.api.response.InterviewStrategyAvailabilityResponse;
import com.sogonsogon.gonggomoon.domain.interviewStrategy.application.InterviewStrategyAvailabilityService;
import com.sogonsogon.gonggomoon.domain.interviewStrategy.application.result.InterviewStrategyAvailabilityResult;
import com.sogonsogon.gonggomoon.global.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class InterviewStrategyAvailabilityController {

    private final InterviewStrategyAvailabilityService interviewStrategyAvailabilityService;

    /**
     * 생성 가능한지 조회합니다.
     */
    @GetMapping("/api/v1/interview-strategies/availability")
    public ResponseEntity<BaseResponse<InterviewStrategyAvailabilityResponse>> getStrategyAvailability(
            @AuthenticationPrincipal AccessUser user
    ) {
        InterviewStrategyAvailabilityResult result = interviewStrategyAvailabilityService.getAvailability(user.getId());

        return ResponseEntity.ok(BaseResponse.success(InterviewStrategyAvailabilityResponse.from(result)));
    }
}
