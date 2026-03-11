package com.sogonsogon.gonggomoon.domain.post.api;

import com.sogonsogon.gonggomoon.domain.post.dto.request.SearchPostRequest;
import com.sogonsogon.gonggomoon.domain.post.application.PostService;
import com.sogonsogon.gonggomoon.global.response.BaseResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public ResponseEntity<BaseResponse<Void>> searchPosts(
            @ModelAttribute SearchPostRequest request,
            Pageable pageable
    ) {
        return ResponseEntity.ok(BaseResponse.success());
    }
}
