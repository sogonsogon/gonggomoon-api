package com.sogonsogon.gonggomoon.domain.ai.application;

import com.sogonsogon.gonggomoon.domain.ai.domain.AiCallingType;
import com.sogonsogon.gonggomoon.domain.ai.domain.ExtractedExperience;
import com.sogonsogon.gonggomoon.domain.ai.domain.ExtractedExperienceRepository;
import com.sogonsogon.gonggomoon.domain.ai.dto.request.ExperienceExtractRequest;
import com.sogonsogon.gonggomoon.domain.ai.dto.response.ExperienceExtractResponse;
import com.sogonsogon.gonggomoon.domain.ai.infrastructure.AiServerClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiService {

    private final ExtractedExperienceRepository extractedExperienceRepository;
    private final AiServerClient aiServerClient;

    /*
    * AI 서버에 경험 추출 요청을 처리하는 비즈니스 로직
    *
    * @param request 경험 추출 요청 DTO
    * @return 경험 추출 응답 DTO
    * */
    public ExperienceExtractResponse requestExperienceExtraction(ExperienceExtractRequest request) {

        // ExtractedExperience 엔티티 생성
        ExtractedExperience newExtractedExperience = ExtractedExperience.create(request.userId(), request.fileAssetId());
        ExtractedExperience savedExtractedExperience = extractedExperienceRepository.save(newExtractedExperience);


        aiServerClient.requestExperienceExtraction(savedExtractedExperience.getId());

        return new ExperienceExtractResponse(AiCallingType.EXTRACT_EXPERIENCE.name(),savedExtractedExperience.getId());
    }

}
