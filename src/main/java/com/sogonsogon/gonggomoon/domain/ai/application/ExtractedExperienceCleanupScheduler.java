package com.sogonsogon.gonggomoon.domain.ai.application;

import com.sogonsogon.gonggomoon.domain.ai.domain.AiUsageType;
import com.sogonsogon.gonggomoon.domain.ai.domain.ExtractedExperience;
import com.sogonsogon.gonggomoon.domain.ai.domain.ExtractedExperienceRepository;
import com.sogonsogon.gonggomoon.domain.ai.domain.ExtractionStatus;
import com.sogonsogon.gonggomoon.domain.file.application.FileAssetService;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 경험 추출 임시 파일 정리 스케줄러 (안전망)
 * <p>
 * 정상 흐름에서는 추출 완료(콜백) 시점에 임시 파일이 삭제된다. 그러나 워커가 콜백을 보내기 전에
 * 크래시하거나 콜백 자체가 유실되는 경우, ExtractedExperience가 PROCESSING 상태로 남고 S3 파일도
 * 고아로 남을 수 있다. 이 스케줄러는 일정 시간 이상 PROCESSING으로 멈춰 있는 작업을 찾아
 * 임시 파일을 정리하고 작업을 FAILED로 마감하며 사용량을 환불한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ExtractedExperienceCleanupScheduler {

    private final ExtractedExperienceRepository extractedExperienceRepository;
    private final FileAssetService fileAssetService;
    private final AiUsagePolicyService aiUsagePolicyService;

    @Value("${experience.extraction.cleanup.enabled:true}")
    private boolean cleanupEnabled;

    /**
     * PROCESSING 상태로 이 시간(분)을 초과해 멈춰 있는 작업을 정리 대상으로 본다.
     * 워커 처리 시간 + Cloud Tasks 재시도(백오프)를 충분히 넘는 값으로 둔다.
     */
    @Value("${experience.extraction.cleanup.stuck-threshold-minutes:120}")
    private long stuckThresholdMinutes;

    @Scheduled(cron = "${experience.extraction.cleanup.cron:0 */30 * * * *}")
    public void cleanupStuckExtractions() {
        if (!cleanupEnabled) {
            return;
        }

        Instant threshold = Instant.now().minus(Duration.ofMinutes(stuckThresholdMinutes));
        List<ExtractedExperience> stuckExtractions =
            extractedExperienceRepository.findAllByStatusAndCreatedAtBefore(ExtractionStatus.PROCESSING, threshold);

        if (stuckExtractions.isEmpty()) {
            return;
        }

        log.warn("멈춘 경험 추출 작업 정리 시작 count={}, thresholdMinutes={}", stuckExtractions.size(), stuckThresholdMinutes);

        for (ExtractedExperience extraction : stuckExtractions) {
            try {
                cleanup(extraction);
            } catch (RuntimeException exception) {
                log.error("멈춘 경험 추출 작업 정리 실패 extractedExperienceId={}", extraction.getId(), exception);
            }
        }
    }

    private void cleanup(ExtractedExperience extraction) {
        fileAssetService.deleteTemporaryFiles(List.of(extraction.getFileAssetId()));

        extraction.updateStatus(ExtractionStatus.FAILED);
        extractedExperienceRepository.save(extraction);

        aiUsagePolicyService.refund(
            extraction.getUserId(),
            AiUsageType.EXPERIENCE_EXTRACTION,
            extraction.getGeneratedDate()
        );

        log.info("멈춘 경험 추출 작업 정리 완료 extractedExperienceId={}, fileAssetId={}",
            extraction.getId(), extraction.getFileAssetId());
    }
}
