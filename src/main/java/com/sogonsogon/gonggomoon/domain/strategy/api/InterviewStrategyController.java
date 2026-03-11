package com.sogonsogon.gonggomoon.domain.strategy.api;

import com.sogonsogon.gonggomoon.domain.auth.infrastructure.security.AccessUser;
import com.sogonsogon.gonggomoon.domain.strategy.api.request.GenerateInterviewQuestionSetRequest;
import com.sogonsogon.gonggomoon.domain.strategy.api.response.GenerateInterviewQuestionSetResponse;
import com.sogonsogon.gonggomoon.domain.strategy.application.InterviewStrategyService;
import com.sogonsogon.gonggomoon.domain.strategy.application.result.GenerateInterviewQuestionSetResult;
import com.sogonsogon.gonggomoon.global.response.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
}
