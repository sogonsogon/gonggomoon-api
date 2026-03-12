package com.sogonsogon.gonggomoon.domain.strategy.api;

import com.sogonsogon.gonggomoon.domain.auth.infrastructure.security.AccessUser;
import com.sogonsogon.gonggomoon.domain.strategy.api.response.InterviewQuestionSetListResponse;
import com.sogonsogon.gonggomoon.domain.strategy.api.request.GenerateInterviewQuestionSetRequest;
import com.sogonsogon.gonggomoon.domain.strategy.api.response.GenerateInterviewQuestionSetResponse;
import com.sogonsogon.gonggomoon.domain.strategy.api.response.InterviewStrategyDetailResponse;
import com.sogonsogon.gonggomoon.domain.strategy.application.InterviewStrategyService;
import com.sogonsogon.gonggomoon.domain.strategy.application.result.GenerateInterviewQuestionSetResult;
import com.sogonsogon.gonggomoon.domain.strategy.application.result.InterviewQuestionSetListResult;
import com.sogonsogon.gonggomoon.domain.strategy.application.result.InterviewStrategyDetailResult;
import com.sogonsogon.gonggomoon.global.response.BaseResponse;
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

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class InterviewStrategyController {
    private final InterviewStrategyService interviewStrategyService;

    /**
     * 면접 전략 질문 세트를 생성합니다.
     */
    @PostMapping("/interviews")
    public ResponseEntity<BaseResponse<GenerateInterviewQuestionSetResponse>> generate(
            @AuthenticationPrincipal AccessUser user,
            @RequestBody @Valid GenerateInterviewQuestionSetRequest req
    ) {
        GenerateInterviewQuestionSetResult result = interviewStrategyService.generate(user.getId(), req);

        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.success(GenerateInterviewQuestionSetResponse.from(result)));
    }

    /**
     * 면접 전략 질문 세트 목록을 조회합니다.
     */
    @GetMapping("/interviews")
    public ResponseEntity<BaseResponse<InterviewQuestionSetListResponse>> getList(
            @AuthenticationPrincipal AccessUser user
    ) {
        InterviewQuestionSetListResult result = interviewStrategyService.getInterviewStrategiesList(user.getId());

       return ResponseEntity.ok(BaseResponse.success(InterviewQuestionSetListResponse.from(result)));
    }

    /**
     * 면접 전략 질문 세트 상세를 조회합니다.
     */
    @GetMapping("/interviews/{interviewStrategyId}")
    public ResponseEntity<BaseResponse<InterviewStrategyDetailResponse>> getInterviewStrategyDetail (
            @AuthenticationPrincipal AccessUser user,
            @PathVariable("interviewStrategyId") Long interviewStrategyId) {
        InterviewStrategyDetailResult result = interviewStrategyService.getInterviewStrategyDetail(interviewStrategyId, user.getId());

        return ResponseEntity.ok(BaseResponse.success(InterviewStrategyDetailResponse.from(result)));
    }

    /**
     * 면접 전략 질문 세트를 삭제합니다.
     */
    @DeleteMapping("/interviews/{interviewStrategyId}")
    public ResponseEntity<BaseResponse<Void>> deleteInterviewStrategy(@AuthenticationPrincipal AccessUser user,
                                                                      @PathVariable("interviewStrategyId") Long interviewStrategyId) {
        interviewStrategyService.deleteInterviewStrategy(interviewStrategyId, user.getId());

        return ResponseEntity.ok(BaseResponse.success());
    }
}
