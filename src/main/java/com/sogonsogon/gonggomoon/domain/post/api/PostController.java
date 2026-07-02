package com.sogonsogon.gonggomoon.domain.post.api;

import com.sogonsogon.gonggomoon.domain.auth.infrastructure.security.AccessUser;
import com.sogonsogon.gonggomoon.domain.post.application.PostService;
import com.sogonsogon.gonggomoon.domain.post.dto.request.SummaryRequest;
import com.sogonsogon.gonggomoon.domain.post.dto.response.SummaryResponse;
import com.sogonsogon.gonggomoon.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "공고", description = "공고 조회 및 검색 관련 API")
@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<BaseResponse<SummaryResponse>> extractAndRefined(@Valid @RequestBody SummaryRequest request,
                                                                           @AuthenticationPrincipal AccessUser user) {
        SummaryResponse response = postService.extractAndRefinedPost(request, user.getId());

        return ResponseEntity.ok(BaseResponse.success(response));
    }

}
