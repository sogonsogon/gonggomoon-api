package com.sogonsogon.gonggomoon.domain.experience.application;

import com.sogonsogon.gonggomoon.domain.ai.application.AiService;
import com.sogonsogon.gonggomoon.domain.ai.dto.response.ExperienceExtractResponse;
import com.sogonsogon.gonggomoon.domain.experience.api.request.ExperienceExtractRequest;
import com.sogonsogon.gonggomoon.domain.experience.application.result.ExperienceExtractionResult;
import com.sogonsogon.gonggomoon.domain.experience.domain.FileAsset;
import com.sogonsogon.gonggomoon.domain.experience.domain.FileAssetRepository;
import com.sogonsogon.gonggomoon.domain.experience.error.ExperienceErrorCode;
import com.sogonsogon.gonggomoon.global.error.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExperienceExtractionService {

    private static final int MAX_FILE_ASSET_COUNT = 2;

    private final AiService aiService;
    private final FileAssetRepository fileAssetRepository;

    public ExperienceExtractionResult startExperienceExtraction (ExperienceExtractRequest req, Long userId) {
        validateRequestFileAssetIds(req.fileAssetIds());

        List<FileAsset> fileAssets = fileAssetRepository.findAllByIdInAndUserId(req.fileAssetIds(), userId);

        // 존재하지 않는 파일이 있거나, 다른 유저 파일이 섞였을 때
        if (fileAssets.size() != req.fileAssetIds().size()) {
            throw new BaseException(ExperienceErrorCode.INVALID_FILE_ASSET_REQUEST);
        }

        ExperienceExtractResponse aiResponse = aiService.requestExperienceExtraction(userId, req.fileAssetIds());

        return ExperienceExtractionResult.from(aiResponse.extractedExperienceIds());
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
