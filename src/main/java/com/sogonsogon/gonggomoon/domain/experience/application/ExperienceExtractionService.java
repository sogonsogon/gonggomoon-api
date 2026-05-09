package com.sogonsogon.gonggomoon.domain.experience.application;

import com.sogonsogon.gonggomoon.domain.ai.application.AiService;
import com.sogonsogon.gonggomoon.domain.ai.application.AiUsagePolicyService;
import com.sogonsogon.gonggomoon.domain.ai.domain.ExperienceItem;
import com.sogonsogon.gonggomoon.domain.ai.domain.Experiences;
import com.sogonsogon.gonggomoon.domain.ai.domain.ExtractedExperience;
import com.sogonsogon.gonggomoon.domain.ai.domain.ExtractedExperienceRepository;
import com.sogonsogon.gonggomoon.domain.ai.domain.AiUsageType;
import com.sogonsogon.gonggomoon.domain.ai.dto.response.ExperienceExtractResponse;
import com.sogonsogon.gonggomoon.domain.ai.error.ExtractedExperienceErrorCode;
import com.sogonsogon.gonggomoon.domain.experience.api.request.ExperienceExtractRequest;
import com.sogonsogon.gonggomoon.domain.experience.application.result.ExperienceExtractionResult;
import com.sogonsogon.gonggomoon.domain.experience.application.result.ExperienceExtractionSearchResult;
import com.sogonsogon.gonggomoon.domain.file.domain.FileAsset;
import com.sogonsogon.gonggomoon.domain.file.domain.FileAssetRepository;
import com.sogonsogon.gonggomoon.domain.experience.error.ExperienceErrorCode;
import com.sogonsogon.gonggomoon.global.error.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExperienceExtractionService {

    private static final int MAX_FILE_ASSET_COUNT = 2;

    private final AiService aiService;
    private final FileAssetRepository fileAssetRepository;
    private final ExtractedExperienceRepository extractedExperienceRepository;
    private final AiUsagePolicyService aiUsagePolicyService;

    @Value("${experience.extraction.weekly-limit-enabled:true}")
    private boolean weeklyLimitEnabled;

    /*
    * AI 경험 추출 요청 처리
    * */
    public ExperienceExtractionResult startExperienceExtraction (ExperienceExtractRequest req, Long userId) {
        validateRequestFileAssetIds(req.fileAssetIds());
        List<FileAsset> fileAssets = fileAssetRepository.findAllByIdInAndUserId(req.fileAssetIds(), userId);

        // 존재하지 않는 파일이 있거나, 다른 유저 파일이 섞였을 때
        if (fileAssets.size() != req.fileAssetIds().size()) {
            throw new BaseException(ExperienceErrorCode.INVALID_FILE_ASSET_REQUEST);
        }

        if (weeklyLimitEnabled && !aiUsagePolicyService.reserve(userId, AiUsageType.EXPERIENCE_EXTRACTION)) {
            throw new BaseException(ExperienceErrorCode.WEEKLY_LIMIT_EXCEEDED);
        }

        ExperienceExtractResponse aiResponse;
        try {
            aiResponse = aiService.requestExperienceExtraction(userId, req.fileAssetIds());
        } catch (RuntimeException exception) {
            if (weeklyLimitEnabled) {
                aiUsagePolicyService.refund(userId, AiUsageType.EXPERIENCE_EXTRACTION, aiUsagePolicyService.currentWeekStartDate());
            }
            throw exception;
        }

        return ExperienceExtractionResult.from(aiResponse.extractedExperienceIds());
    }

    /*
    * 추출된 경험 조회 요청 처리
    * */
    public ExperienceExtractionSearchResult getExperienceExtraction(Long extractionId, Long userId) {
        ExtractedExperience foundData = extractedExperienceRepository.findByUserIdAndId(userId, extractionId).orElseThrow(
            () -> new BaseException(ExtractedExperienceErrorCode.NOT_FOUND)
        );

        Experiences experiences = foundData.getExperiences();

        if (experiences == null) {
            throw new BaseException(ExtractedExperienceErrorCode.EXPERIENCES_IS_EMPTY);
        }

        List<ExperienceItem> experience_items = experiences.getExperiences();
        return ExperienceExtractionSearchResult.of(experience_items);
    }

    private void validateRequestFileAssetIds(List<Long> fileAssetIds) {
        // 3개 이상 요청했을 때
        if (fileAssetIds.size() > MAX_FILE_ASSET_COUNT) {
            throw new BaseException(ExperienceErrorCode.FILE_ASSET_COUNT_EXCEEDED);
        }

        // [1, 1] 같이 중복 요청했을 때
        if (hasDuplicate(fileAssetIds)) {
            throw new BaseException(ExperienceErrorCode.DUPLICATE_FILE_ASSET_ID);
        }
    }

    private boolean hasDuplicate(List<Long> fileAssetIds) {
        return fileAssetIds.size() != new HashSet<>(fileAssetIds).size();
    }

}
