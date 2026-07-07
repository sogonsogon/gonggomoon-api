package com.sogonsogon.gonggomoon.domain.experience.application;

import com.sogonsogon.gonggomoon.domain.ai.application.AiService;
import com.sogonsogon.gonggomoon.domain.ai.application.AiUsagePolicyService;
import com.sogonsogon.gonggomoon.domain.ai.domain.AiUsageType;
import com.sogonsogon.gonggomoon.domain.ai.dto.response.ExperienceExtractResponse;
import com.sogonsogon.gonggomoon.domain.experience.application.result.ExperienceExtractionResult;
import com.sogonsogon.gonggomoon.domain.experience.error.ExperienceErrorCode;
import com.sogonsogon.gonggomoon.domain.file.api.request.UploadFileRequest;
import com.sogonsogon.gonggomoon.domain.file.application.FileAssetService;
import com.sogonsogon.gonggomoon.global.error.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExperienceExtractionService {

    private final AiService aiService;
    private final FileAssetService fileAssetService;
    private final AiUsagePolicyService aiUsagePolicyService;

    @Value("${experience.extraction.weekly-limit-enabled:true}")
    private boolean weeklyLimitEnabled;

    /*
    * AI 경험 추출 요청 처리
    *
    * 파일을 임시로 업로드한 뒤 AI 추출 작업을 시작한다.
    * 원본 파일은 추출 완료(콜백) 시점에 삭제되며, 실패 시에는 즉시 정리한다.
    * 반환되는 extractionId는 추출 결과가 아니라 작업 핸들이며,
    * 클라이언트는 이 값으로 상태 조회(SSE)를 구독한 뒤 완료 시 경험 리스트를 다시 조회한다.
    * */
    public ExperienceExtractionResult startExperienceExtraction(UploadFileRequest req, MultipartFile file, Long userId) {
        // 1. 주간 사용량 예약 (가장 먼저 차감하여 업로드/발행 실패 시 환불 처리)
        if (weeklyLimitEnabled && !aiUsagePolicyService.reserve(userId, AiUsageType.EXPERIENCE_EXTRACTION)) {
            throw new BaseException(ExperienceErrorCode.WEEKLY_LIMIT_EXCEEDED);
        }

        // 2. 파일 임시 업로드 (S3 + FileAsset 생성)
        Long fileAssetId;
        try {
            fileAssetId = fileAssetService.uploadFile(userId, req, file).fileAssetId();
        } catch (RuntimeException exception) {
            refundUsage(userId);
            throw exception;
        }

        // 3. 추출 작업 생성 + Cloud Tasks 발행
        ExperienceExtractResponse aiResponse;
        try {
            aiResponse = aiService.requestExperienceExtraction(userId, List.of(fileAssetId));
        } catch (RuntimeException exception) {
            refundUsage(userId);
            // 발행에 실패하면 워커가 처리하지 않으므로 임시 파일을 즉시 정리한다.
            fileAssetService.deleteTemporaryFiles(List.of(fileAssetId));
            throw exception;
        }

        // 파일 1건당 추출 작업 1건이므로 단일 작업 ID를 반환한다.
        return ExperienceExtractionResult.from(aiResponse.extractedExperienceIds().get(0));
    }

    private void refundUsage(Long userId) {
        if (weeklyLimitEnabled) {
            aiUsagePolicyService.refund(userId, AiUsageType.EXPERIENCE_EXTRACTION, aiUsagePolicyService.currentWeekStartDate());
        }
    }

}
