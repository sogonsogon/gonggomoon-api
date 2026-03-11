package com.sogonsogon.gonggomoon.domain.post.api;

import com.sogonsogon.gonggomoon.domain.post.dto.request.SearchPostRequest;
import com.sogonsogon.gonggomoon.domain.post.application.PostService;
import com.sogonsogon.gonggomoon.domain.post.dto.response.PostResponse;
import com.sogonsogon.gonggomoon.domain.post.dto.response.PostsResponse;
import com.sogonsogon.gonggomoon.global.response.BaseResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public ResponseEntity<BaseResponse<BaseResponse.PageResponse<PostsResponse>>> searchPosts(
            @ModelAttribute SearchPostRequest request,
            Pageable pageable
    ) {

        Page<PostsResponse> page = postService.searchPosts(request, pageable);

        return ResponseEntity.ok(BaseResponse.success(
                BaseResponse.PageResponse.<PostsResponse>builder()
                        .content(page.getContent())
                        .pageInfo(BaseResponse.PageInfo.builder()
                                .currentPage(page.getNumber())
                                .totalPages(page.getTotalPages())
                                .totalElements(page.getTotalElements())
                                .hasNext(page.hasNext())
                                .build())
                        .build())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<BaseResponse<PostResponse>> getPost(@PathVariable Long id) {

        return ResponseEntity.ok(BaseResponse.success(postService.getPost(id)));
    }
}
