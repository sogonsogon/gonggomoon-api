package com.sogonsogon.gonggomoon.domain.experience.application;

import com.sogonsogon.gonggomoon.domain.experience.api.request.CreateExperienceRequest;
import com.sogonsogon.gonggomoon.domain.experience.api.request.UpdateExperienceRequest;
import com.sogonsogon.gonggomoon.domain.experience.application.result.CreateExperienceResult;
import com.sogonsogon.gonggomoon.domain.experience.application.result.ExperienceDetailResult;
import com.sogonsogon.gonggomoon.domain.experience.application.result.ExperienceListResult;
import com.sogonsogon.gonggomoon.domain.experience.domain.Experience;
import com.sogonsogon.gonggomoon.domain.experience.domain.ExperienceRepository;
import com.sogonsogon.gonggomoon.domain.experience.error.ExperienceErrorCode;
import com.sogonsogon.gonggomoon.global.error.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExperienceService {
    private final ExperienceRepository experienceRepository;

    /**
     * 경험 생성 서비스
     * @param userId
     * @param req
     * @return
     */
    public CreateExperienceResult create(Long userId, CreateExperienceRequest req) {
        Experience experience = Experience.create(userId, req.title(), req.experienceType(), req.experienceContent(), req.startDate(), req.endDate());
        Experience savedExperience = experienceRepository.save(experience);

        return CreateExperienceResult.from(savedExperience);
    }

    /**
     * 경험 수정 서비스
     * @param experienceId
     * @param userId
     * @param req
     * @return
     */
    public ExperienceDetailResult update(Long experienceId, Long userId, UpdateExperienceRequest req) {
        Experience experience = experienceRepository.findByIdAndUserId(experienceId, userId)
                .orElseThrow(() -> new BaseException(ExperienceErrorCode.NOT_FOUND));

        experience.update(req.title(), req.experienceType(), req.experienceContent(), req.startDate(), req.endDate());

        return ExperienceDetailResult.from(experience);
    }

    /**
     * 경험 삭제 서비스
     * @param experienceId
     * @param userId
     */
    public void deleteExperience(Long experienceId, Long userId) {
        Experience experience = experienceRepository.findByIdAndUserId(experienceId, userId)
                .orElseThrow(() -> new BaseException(ExperienceErrorCode.NOT_FOUND));

        experienceRepository.delete(experience);
    }

    /**
     * 경험 목록 조회 서비스
     * @param userId
     * @return
     */
    public ExperienceListResult getExperiencesList(Long userId) {
        List<Experience> experiences = experienceRepository.findAllByUserIdOrderByUpdatedAtDesc(userId);

        return ExperienceListResult.from(experiences);
    }

    /**
     * 경험 상세 조회 서비스
     * @param experienceId
     * @param userId
     * @return
     */
    public ExperienceDetailResult getExperienceDetail(Long experienceId, Long userId) {
        Experience experience = experienceRepository.findByIdAndUserId(experienceId, userId)
                .orElseThrow(() -> new BaseException(ExperienceErrorCode.NOT_FOUND));

        return ExperienceDetailResult.from(experience);
    }
}
