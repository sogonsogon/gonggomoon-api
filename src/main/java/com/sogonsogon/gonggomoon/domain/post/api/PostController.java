package com.sogonsogon.gonggomoon.domain.post.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sogonsogon.gonggomoon.domain.auth.infrastructure.security.AccessUser;
import com.sogonsogon.gonggomoon.domain.post.application.PostService;
import com.sogonsogon.gonggomoon.domain.post.dto.request.PostAnalysisRequest;
import com.sogonsogon.gonggomoon.domain.post.dto.response.PostResponse;
import com.sogonsogon.gonggomoon.global.docs.ErrorResponseExamples;
import com.sogonsogon.gonggomoon.domain.post.dto.response.PostAnalysisResponse;
import com.sogonsogon.gonggomoon.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @Operation(summary = "공고 URL 분석 시작",
            description = "채용 공고 URL의 콘텐츠를 추출하여 AI 분석 작업을 시작합니다. "
                    + "이미 분석된 URL이면 캐시된 결과로 즉시 SUCCESS 상태를 반환하고, "
                    + "그렇지 않으면 PENDING 상태로 응답한 뒤 비동기로 분석이 진행됩니다. "
                    + "응답의 postId로 분석 상태를 조회(SSE)할 수 있습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "공고 분석 작업 시작 성공 (캐시 적중 시 즉시 SUCCESS)"),
            @ApiResponse(responseCode = "400",
                    description = "입력값 검증 실패(GLOBAL_INVALID_INPUT_VALUE) - URL 누락 등",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = ErrorResponseExamples.VALIDATION))),
            @ApiResponse(responseCode = "401", description = "인증 실패 (액세스 토큰 누락/만료)"),
            @ApiResponse(responseCode = "422",
                    description = "URL에서 콘텐츠 추출 실패(EXTRACTION_FAILED)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = ErrorResponseExamples.POST_EXTRACTION_FAILED))),
            @ApiResponse(responseCode = "500",
                    description = "AI 서버 오류(AI_SERVER_ERROR)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = ErrorResponseExamples.AI_SERVER_ERROR)))
    })
    @PostMapping
    public ResponseEntity<BaseResponse<PostAnalysisResponse>> extractAndRefined(@Valid @RequestBody PostAnalysisRequest request,
                                                                                @AuthenticationPrincipal AccessUser user) {
        PostAnalysisResponse response = postService.startPostAnalysis(request, user.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.success(response));
    }

    @Operation(summary = "공고 분석 결과 조회",
            description = "postAnalysisId에 해당하는 공고 분석 결과를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "공고 분석 결과 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패 (액세스 토큰 누락/만료)"),
            @ApiResponse(responseCode = "404",
                    description = "분석 결과가 존재하지 않음(POST_ANALYSIS_NOT_FOUND)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = ErrorResponseExamples.POST_ANALYSIS_NOT_FOUND)))
    })

    @GetMapping("/{postAnalysisId}")
    public ResponseEntity<BaseResponse<PostResponse>> getAnalysis(@PathVariable Long postAnalysisId) throws JsonProcessingException {
        PostResponse response = postService.getAnalysisById(postAnalysisId);
        return ResponseEntity.ok(BaseResponse.success(response));
    }

}
