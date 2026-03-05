package com.sogonsogon.gonggomoon.domain.experience.application;

import com.sogonsogon.gonggomoon.domain.experience.api.request.CreateExperienceRequest;
import com.sogonsogon.gonggomoon.domain.experience.api.request.UpdateExperienceRequest;
import com.sogonsogon.gonggomoon.domain.experience.application.result.CreateExperienceResult;
import com.sogonsogon.gonggomoon.domain.experience.application.result.UpdateExperienceResult;
import com.sogonsogon.gonggomoon.domain.experience.domain.Experience;
import com.sogonsogon.gonggomoon.domain.experience.domain.ExperienceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

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
    public UpdateExperienceResult update(Long experienceId, UpdateExperienceRequest req) {
        Experience experience = experienceRepository.findById(experienceId)
                .orElseThrow(() -> new RuntimeException(String.valueOf(HttpStatus.NOT_FOUND)));

        experience.update(req.title(), req.experienceType(), req.experienceContent(), req.startDate(), req.endDate());

        return UpdateExperienceResult.from(experience);
    }

    // TODO 커스텀 에러코드, Exception으로 수정 예정
    public void deleteExperience(Long experienceId) {
        Experience experience = experienceRepository.findById(experienceId)
                .orElseThrow(() -> new RuntimeException(String.valueOf(HttpStatus.NOT_FOUND)));

        experienceRepository.delete(experience);
    }
}
