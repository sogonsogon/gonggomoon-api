package com.sogonsogon.gonggomoon.domain.ai.application;

import com.sogonsogon.gonggomoon.domain.ai.domain.Experiences;
import com.sogonsogon.gonggomoon.domain.ai.domain.ExtractedExperience;
import com.sogonsogon.gonggomoon.domain.ai.domain.ExtractedExperienceRepository;
import com.sogonsogon.gonggomoon.domain.ai.domain.ExtractionStatus;
import com.sogonsogon.gonggomoon.domain.ai.dto.request.ExperienceExtractionCallbackRequest;
import com.sogonsogon.gonggomoon.domain.ai.error.ExtractedExperienceErrorCode;
import com.sogonsogon.gonggomoon.domain.ai.infrastructure.ExperienceResultMapper;
import com.sogonsogon.gonggomoon.global.error.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AiCallbackService {

    private final ExperienceResultMapper experienceResultMapper;
    private final ExtractedExperienceRepository extractedExperienceRepository;

    @Transactional
    public void createExtractedExperience(ExperienceExtractionCallbackRequest request) {

        // id 값으로 찾아오기
        ExtractedExperience foundExperience =extractedExperienceRepository.findById(request.id()).orElseThrow(
            () -> new BaseException(ExtractedExperienceErrorCode.NOT_FOUND)
        );

        // Experiences로 맵핑해주고, foundExperience에 Experiences 추가하기
        Experiences experiences = experienceResultMapper.toExperiences(request.result());
        foundExperience.updateExperiences(experiences);

        // status를 READY로 업데이트 (완전히 추출이 완료된 상태)
        foundExperience.updateStatus(ExtractionStatus.READY);

        // 명시적으로 업데이트를 표현하기 위해 save() 호출 (영속성 컨텍스트에 의해 자동으로 업데이트가 될 수 있지만, 명시적으로 표현)
        extractedExperienceRepository.save(foundExperience);

    }
}
