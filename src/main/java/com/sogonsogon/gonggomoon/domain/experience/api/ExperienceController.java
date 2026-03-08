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
import com.sogonsogon.gonggomoon.global.response.BaseResponse;
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

@RestController
@RequestMapping("/api/v1/experiences")
@RequiredArgsConstructor
public class ExperienceController {

    private final ExperienceService experienceService;

    /**
     * 경험을 수기로 생성합니다.
     * @param req
     * @return
     */
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
    @PatchMapping("/{experienceId}")
    public ResponseEntity<BaseResponse<ExperienceDetailResponse>> updateExperience(@AuthenticationPrincipal AccessUser user,
                                                                   @PathVariable("experienceId") Long experienceId,
                                                                   @RequestBody @Valid UpdateExperienceRequest req) {
        ExperienceDetailResult result = experienceService.update(experienceId, user.getId(), req);

        return ResponseEntity.ok(BaseResponse.success(ExperienceDetailResponse.from(result)));
    }

    /**
     * 경험을 삭제합니다.
     * @param experienceId
     * @return
     */
    @DeleteMapping("/{experienceId}")
    public ResponseEntity<BaseResponse<Void>> deleteExperience(@AuthenticationPrincipal AccessUser user,
                                               @PathVariable("experienceId") Long experienceId) {
        experienceService.deleteExperience(experienceId, user.getId());

        return ResponseEntity.ok(BaseResponse.success());
    }

    /**
     * 경험 목록을 조회합니다.
     */
    @GetMapping
    public ResponseEntity<BaseResponse<ExperienceListResponse>> getExperiencesList(@AuthenticationPrincipal AccessUser user) {
        ExperienceListResult result = experienceService.getExperiencesList(user.getId());

        return ResponseEntity.ok(BaseResponse.success(ExperienceListResponse.from(result)));
    }

    /**
     * 경험 상세를 조회합니다.
     */
    @GetMapping("/{experienceId}")
    public ResponseEntity<BaseResponse<ExperienceDetailResponse>> getExperienceDetail(@AuthenticationPrincipal AccessUser user,
                                                                                      @PathVariable("experienceId") Long experienceId) {
        ExperienceDetailResult result = experienceService.getExperienceDetail(experienceId, user.getId());

        return ResponseEntity.ok(BaseResponse.success(ExperienceDetailResponse.from(result)));
    }
}
