package com.sogonsogon.gonggomoon.domain.experience.application;

import com.sogonsogon.gonggomoon.domain.experience.api.request.CreateExperienceRequest;
import com.sogonsogon.gonggomoon.domain.experience.api.request.UpdateExperienceRequest;
import com.sogonsogon.gonggomoon.domain.experience.application.result.CreateExperienceResult;
import com.sogonsogon.gonggomoon.domain.experience.application.result.ExperienceDetailResult;
import com.sogonsogon.gonggomoon.domain.experience.application.result.ExperienceListResult;
import com.sogonsogon.gonggomoon.domain.experience.domain.Experience;
import com.sogonsogon.gonggomoon.domain.experience.domain.ExperienceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExperienceService {
    private final ExperienceRepository experienceRepository;

    public CreateExperienceResult create(CreateExperienceRequest req) {
        Experience experience = Experience.create(req.title(), req.experienceType(), req.experienceContent(), req.startDate(), req.endDate());
        Experience savedExperience = experienceRepository.save(experience);

        return CreateExperienceResult.from(savedExperience);
    }

    // TODO 커스텀 에러코드, Exception으로 수정 예정
    public ExperienceDetailResult update(Long experienceId, Long userId, UpdateExperienceRequest req) {
        Experience experience = experienceRepository.findByIdAndUserId(experienceId, userId)
                .orElseThrow(() -> new RuntimeException(String.valueOf(HttpStatus.NOT_FOUND)));

        experience.update(req.title(), req.experienceType(), req.experienceContent(), req.startDate(), req.endDate());

        return ExperienceDetailResult.from(experience);
    }

    // TODO 커스텀 에러코드, Exception으로 수정 예정
    public void deleteExperience(Long experienceId, Long userId) {
        Experience experience = experienceRepository.findByIdAndUserId(experienceId, userId)
                .orElseThrow(() -> new RuntimeException(String.valueOf(HttpStatus.NOT_FOUND)));

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
                .orElseThrow(() -> new RuntimeException(String.valueOf(HttpStatus.NOT_FOUND)));

        return ExperienceDetailResult.from(experience);
    }
}
