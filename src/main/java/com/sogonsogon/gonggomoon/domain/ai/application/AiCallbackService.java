package com.sogonsogon.gonggomoon.domain.ai.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.sogonsogon.gonggomoon.domain.ai.domain.Experiences;
import com.sogonsogon.gonggomoon.domain.ai.domain.ExtractedExperience;
import com.sogonsogon.gonggomoon.domain.ai.domain.ExtractedExperienceRepository;
import com.sogonsogon.gonggomoon.domain.ai.domain.ExtractionStatus;
import com.sogonsogon.gonggomoon.domain.ai.dto.request.BaseCallbackRequest;
import com.sogonsogon.gonggomoon.domain.ai.error.ExtractedExperienceErrorCode;
import com.sogonsogon.gonggomoon.domain.ai.infrastructure.ExperienceResultMapper;
import com.sogonsogon.gonggomoon.domain.strategy.domain.PortfolioStrategy;
import com.sogonsogon.gonggomoon.domain.strategy.domain.PortfolioStrategyRepository;
import com.sogonsogon.gonggomoon.global.error.BaseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class AiCallbackService {

    private final ExperienceResultMapper experienceResultMapper;
    private final ExtractedExperienceRepository extractedExperienceRepository;
    private final PortfolioStrategyRepository portfolioStrategyRepository;

    @Transactional
    public void createExtractedExperience(BaseCallbackRequest request) {
        JsonNode resultsNode = request.result();

        if (resultsNode == null || !resultsNode.isArray()) {
            throw new BaseException(ExtractedExperienceErrorCode.INVALID_RESULT_FORMAT);
        }

        List<JsonNode> callbackItems = new ArrayList<>();
        List<Long> ids = new ArrayList<>();

        for (JsonNode itemNode : resultsNode) {
            long extractedExperienceId = itemNode.path("extracted_experience_id").asLong(0);

            if (extractedExperienceId == 0) {
                throw new BaseException(ExtractedExperienceErrorCode.INVALID_RESULT_FORMAT);
            }

            callbackItems.add(itemNode);
            ids.add(extractedExperienceId);
        }

        List<ExtractedExperience> foundExperiences = extractedExperienceRepository.findAllById(ids);

        Map<Long, ExtractedExperience> experienceMap = foundExperiences.stream()
            .collect(Collectors.toMap(ExtractedExperience::getId, Function.identity()));

        List<ExtractedExperience> entitiesToSave = new ArrayList<>();

        for (JsonNode itemNode : callbackItems) {
            long extractedExperienceId = itemNode.path("extracted_experience_id").asLong();

            ExtractedExperience foundExperience = experienceMap.get(extractedExperienceId);
            if (foundExperience == null) {
                throw new BaseException(ExtractedExperienceErrorCode.NOT_FOUND);
            }

            Experiences experiences = experienceResultMapper.toExperiencesFromCallbackItem(itemNode);

            foundExperience.updateExperiences(experiences);
            foundExperience.updateStatus(ExtractionStatus.READY);

            entitiesToSave.add(foundExperience);
        }

        extractedExperienceRepository.saveAll(entitiesToSave);
    }

    @Transactional
    public void updatePortfolioStrategy(BaseCallbackRequest request) {

        // id 값으로 찾아오기
        PortfolioStrategy fountStrategy =portfolioStrategyRepository.findByIdAndUserId(request.id(), request.userId()).orElseThrow(
            () -> new BaseException(ExtractedExperienceErrorCode.NOT_FOUND) // TODO : 적절한 에러코드로 변경
        );

        // 결과를 텍스트로 저장 (내부적으로 status 업데이트도 같이 이루어짐)
        System.out.println("AI 콜백으로 받은 포트폴리오 전략 결과: " + request.result().get("portfolioStrategy").toString());
        fountStrategy.addResult(request.result().get("portfolioStrategy").toString());


        // 명시적으로 업데이트를 표현하기 위해 save() 호출 (영속성 컨텍스트에 의해 자동으로 업데이트가 될 수 있지만, 명시적으로 표현)
        portfolioStrategyRepository.save(fountStrategy);

    }
}
