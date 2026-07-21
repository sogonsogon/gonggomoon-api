package com.sogonsogon.gonggomoon.domain.experience.api;

import com.sogonsogon.gonggomoon.domain.auth.infrastructure.security.AccessUser;
import com.sogonsogon.gonggomoon.domain.experience.api.request.CreateExperienceRequest;
import com.sogonsogon.gonggomoon.domain.experience.api.request.UpdateExperienceRequest;
import com.sogonsogon.gonggomoon.domain.experience.api.response.CreateExperienceResponse;
import com.sogonsogon.gonggomoon.domain.experience.api.response.ExperienceDetailResponse;
import com.sogonsogon.gonggomoon.domain.experience.api.response.ExperienceListResponse;
import com.sogonsogon.gonggomoon.domain.experience.application.ExperienceService;
import com.sogonsogon.gonggomoon.domain.experience.application.result.CreateExperienceResult;
import com.sogonsogon.gonggomoon.domain.experience.application.result.ExperienceDetailResult;
import com.sogonsogon.gonggomoon.domain.experience.application.result.ExperienceListResult;
import com.sogonsogon.gonggomoon.global.docs.ErrorResponseExamples;
import com.sogonsogon.gonggomoon.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/experiences")
@RequiredArgsConstructor
@Tag(name = "경험", description = "사용자의 경험을 생성, 수정, 삭제 및 조회하는 API")
public class ExperienceController {

    private final ExperienceService experienceService;

    /**
     * 경험을 수기로 생성합니다.
     * @param req
     * @return
     */
    @Operation(summary = "경험 생성", description = "로그인한 사용자가 입력한 내용을 기반으로 경험을 수기로 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "경험 생성 성공"),
            @ApiResponse(responseCode = "400",
                    description = "입력값 검증 실패(GLOBAL_INVALID_INPUT_VALUE) / 종료일이 시작일보다 이전(EXPERIENCE_INVALID_DATE_RANGE)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = ErrorResponseExamples.VALIDATION))),
            @ApiResponse(responseCode = "401", description = "인증 실패 (액세스 토큰 누락/만료)")
    })
    @PostMapping
    public ResponseEntity<BaseResponse<CreateExperienceResponse>> createExperience(@AuthenticationPrincipal AccessUser user,
                                                                                  @RequestBody @Valid CreateExperienceRequest req) {
        CreateExperienceResult result = experienceService.create(user.getId(), req);
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponse.success(CreateExperienceResponse.from(result)));
    }

    /**
     * 경험을 수정합니다.
     * @param experienceId
     * @param req
     * @return
     */
    @Operation(summary = "경험 수정", description = "경험 ID에 해당하는 경험을 요청 내용으로 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "경험 수정 성공"),
            @ApiResponse(responseCode = "400",
                    description = "입력값 검증 실패(GLOBAL_INVALID_INPUT_VALUE) / 종료일이 시작일보다 이전(EXPERIENCE_INVALID_DATE_RANGE)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = ErrorResponseExamples.VALIDATION))),
            @ApiResponse(responseCode = "401", description = "인증 실패 (액세스 토큰 누락/만료)"),
            @ApiResponse(responseCode = "404",
                    description = "경험을 찾을 수 없거나 본인 소유가 아님(EXPERIENCE_NOT_FOUND)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(value = ErrorResponseExamples.EXPERIENCE_NOT_FOUND)))
    })
    @PatchMapping("/{experienceId}")
    public ResponseEntity<BaseResponse<ExperienceDetailResponse>> updateExperience(@AuthenticationPrincipal AccessUser user,
                                                                   @PathVariable("experienceId") UUID experienceId,
                                                                   @RequestBody @Valid UpdateExperienceRequest req) {
        ExperienceDetailResult result = experienceService.update(experienceId, user.getId(), req);

        return ResponseEntity.ok(BaseResponse.success(ExperienceDetailResponse.from(result)));
    }

    /**
     * 경험을 삭제합니다.
     * @param experienceId
     * @return
     */
    @Deprecated
    @Hidden
    @Operation(summary = "경험 삭제", description = "경험 ID에 해당하는 경험을 삭제합니다.")
    @DeleteMapping("/{experienceId}")
    public ResponseEntity<BaseResponse<Void>> deleteExperience(@AuthenticationPrincipal AccessUser user,
                                               @PathVariable("experienceId") UUID experienceId) {
        experienceService.deleteExperience(experienceId, user.getId());

        return ResponseEntity.ok(BaseResponse.success());
    }

    /**
     * 경험 목록을 조회합니다.
     */
    @Operation(summary = "경험 목록 조회", description = "로그인한 사용자가 등록한 경험 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "경험 목록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패 (액세스 토큰 누락/만료)")
    })
    @GetMapping
    public ResponseEntity<BaseResponse<ExperienceListResponse>> getExperiencesList(@AuthenticationPrincipal AccessUser user) {
        ExperienceListResult result = experienceService.getExperiencesList(user.getId());

        return ResponseEntity.ok(BaseResponse.success(ExperienceListResponse.from(result)));
    }

    /**
     * 경험 상세를 조회합니다.
     */
    @Deprecated
    @Hidden
    @Operation(summary = "경험 상세 조회", description = "경험 ID에 해당하는 경험의 상세 정보를 조회합니다.")
    @GetMapping("/{experienceId}")
    public ResponseEntity<BaseResponse<ExperienceDetailResponse>> getExperienceDetail(@AuthenticationPrincipal AccessUser user,
                                                                                      @PathVariable("experienceId") UUID experienceId) {
        ExperienceDetailResult result = experienceService.getExperienceDetail(experienceId, user.getId());

        return ResponseEntity.ok(BaseResponse.success(ExperienceDetailResponse.from(result)));
    }
}
