package com.sogonsogon.gonggomoon.domain.ai.application;

import com.sogonsogon.gonggomoon.domain.ai.domain.AiCallingType;
import com.sogonsogon.gonggomoon.domain.ai.domain.ExtractedExperience;
import com.sogonsogon.gonggomoon.domain.ai.domain.ExtractedExperienceRepository;
import com.sogonsogon.gonggomoon.domain.ai.dto.request.ExperienceExtractRequest;
import com.sogonsogon.gonggomoon.domain.ai.dto.request.ExperienceExtractionAiServerRequest;
import com.sogonsogon.gonggomoon.domain.ai.dto.request.PortfolioStrategyRequest;
import com.sogonsogon.gonggomoon.domain.ai.dto.response.ExperienceExtractResponse;
import com.sogonsogon.gonggomoon.domain.ai.infrastructure.AiServerClient;
import com.sogonsogon.gonggomoon.domain.experience.domain.Experience;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

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
    public ExperienceExtractResponse requestExperienceExtraction(Long userId, Long fileAssetId) {

         // DTO 생성
        ExperienceExtractRequest request = new ExperienceExtractRequest(userId, fileAssetId);

        // ExtractedExperience 엔티티 생성
        ExtractedExperience newExtractedExperience = ExtractedExperience.create(request.userId(), request.fileAssetId());
        ExtractedExperience savedExtractedExperience = extractedExperienceRepository.save(newExtractedExperience);

        // AI 서버에 경험 추출 요청 전송
        ExperienceExtractionAiServerRequest aiServerRequest = new ExperienceExtractionAiServerRequest(savedExtractedExperience.getId());
        aiServerClient.requestExperienceExtraction(aiServerRequest);

        return new ExperienceExtractResponse(AiCallingType.EXTRACT_EXPERIENCE.name(),savedExtractedExperience.getId());
    }

    /*
    * AI 서버에 포트폴리오 전략 생성 요청을 처리하는 비즈니스 로직
    *
    * @param request 포트폴리오 전략 생성 요청 DTO
    * @return void (전략 생성 결과는 AI 서버에서 비동기로 처리될 예정)
    * */
    public void requestPortfolioStrategyGeneration(
        Long userId,
        Long portfolioStrategyId,
        List<Experience> experiences,
        String positionType,
        String industryType) {

        // DTO 생성
        PortfolioStrategyRequest request = new PortfolioStrategyRequest(
            userId,
            portfolioStrategyId,
            experiences,
            positionType,
            industryType
        );

        // AI 서버에 포트폴리오 전략 생성 요청 전송
        aiServerClient.requestPortfolioStrategyGeneration(request);
    }
}
