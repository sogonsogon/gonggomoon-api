package com.sogonsogon.gonggomoon.domain.post.api;

import com.sogonsogon.gonggomoon.domain.auth.infrastructure.security.AccessUser;
import com.sogonsogon.gonggomoon.domain.post.dto.request.SubmitPostRequest;
import com.sogonsogon.gonggomoon.domain.post.application.SubmissionService;
import com.sogonsogon.gonggomoon.global.response.BaseResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/submssions")
public class SubmissionController {

    private final SubmissionService submissionService;

    public SubmissionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @PostMapping
    public ResponseEntity<BaseResponse<Void>> submitPost(@RequestBody @Valid SubmitPostRequest request,
                                                         @AuthenticationPrincipal AccessUser user) {

        submissionService.submitPost(request, user.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.success());
    }
}
