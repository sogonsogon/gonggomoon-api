package com.sogonsogon.gonggomoon.domain.post.api;

import com.sogonsogon.gonggomoon.domain.post.dto.request.SearchPostRequest;
import com.sogonsogon.gonggomoon.domain.post.application.PostService;
import com.sogonsogon.gonggomoon.domain.post.dto.response.PostResponse;
import com.sogonsogon.gonggomoon.domain.post.dto.response.PostsResponse;
import com.sogonsogon.gonggomoon.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Hidden
@Deprecated
@Tag(name = "공고", description = "공고 조회 및 검색 관련 API")
@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

//    private final PostService postService;
//
//    public PostController(PostService postService) {
//        this.postService = postService;
//    }
//
//    @Operation(summary = "공고 목록 검색", description = "검색 조건과 페이징 정보를 받아 공고 목록을 페이지 단위로 조회합니다.")
//    @GetMapping
//    public ResponseEntity<BaseResponse<BaseResponse.PageResponse<PostsResponse>>> searchPosts(
//            @ModelAttribute SearchPostRequest request,
//            Pageable pageable
//    ) {
//
//        Page<PostsResponse> page = postService.searchPosts(request, pageable);
//
//        return ResponseEntity.ok(BaseResponse.success(
//                BaseResponse.PageResponse.<PostsResponse>builder()
//                        .content(page.getContent())
//                        .pageInfo(BaseResponse.PageInfo.builder()
//                                .currentPage(page.getNumber())
//                                .totalPages(page.getTotalPages())
//                                .totalElements(page.getTotalElements())
//                                .hasNext(page.hasNext())
//                                .build())
//                        .build())
//        );
//    }
//
//    @Operation(summary = "공고 상세 조회", description = "공고 ID로 단일 공고의 상세 정보를 조회합니다.")
//    @GetMapping("/{id}")
//    public ResponseEntity<BaseResponse<PostResponse>> getPost(@PathVariable Long id) {
//
//        return ResponseEntity.ok(BaseResponse.success(postService.getPost(id)));
//    }
}
