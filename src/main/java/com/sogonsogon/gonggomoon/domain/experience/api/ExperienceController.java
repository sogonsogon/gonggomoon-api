package com.sogonsogon.gonggomoon.domain.experience.api;

import com.sogonsogon.gonggomoon.domain.experience.api.request.CreateExperienceRequest;
import com.sogonsogon.gonggomoon.domain.experience.api.request.UpsertExperienceRequest;
import com.sogonsogon.gonggomoon.domain.experience.api.response.CreateExperienceResponse;
import com.sogonsogon.gonggomoon.domain.experience.api.response.UpsertExperienceResponse;
import com.sogonsogon.gonggomoon.domain.experience.application.ExperienceService;
import com.sogonsogon.gonggomoon.domain.experience.application.result.CreateExperienceResult;
import com.sogonsogon.gonggomoon.domain.experience.application.result.UpsertExperienceResult;
import com.sogonsogon.gonggomoon.global.response.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ExperienceController {

    private final ExperienceService experienceService;

    /**
     * 경험을 수기로 생성합니다.
     * @param req
     * @return
     */
    @PostMapping("/experiences")
    public BaseResponse<CreateExperienceResponse> createExperience(@RequestBody @Valid CreateExperienceRequest req) {
        CreateExperienceResult result = experienceService.create(req);

        return BaseResponse.success(CreateExperienceResponse.from(result));
    }

    /**
     * 경험을 수정합니다.
     * @param experienceId
     * @param req
     * @return
     */
    @PatchMapping("/experiences/{experienceId}")
    public BaseResponse<UpsertExperienceResponse> upsertExperience(@PathVariable("experienceId") Long experienceId, @RequestBody @Valid UpsertExperienceRequest req) {
        UpsertExperienceResult result = experienceService.upsert(experienceId, req);

        return BaseResponse.success(UpsertExperienceResponse.from(result));
    }

    /**
     * 경험을 삭제합니다.
     * @param experienceId
     * @return
     */
    @DeleteMapping("/experiences/{experienceId}")
    public BaseResponse<Void> deleteExperience(@PathVariable("experienceId") Long experienceId) {
        experienceService.deleteExperience(experienceId);

        return BaseResponse.success();
    }
}
