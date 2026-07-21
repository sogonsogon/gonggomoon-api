package com.sogonsogon.gonggomoon.domain.experience.application;

import com.sogonsogon.gonggomoon.domain.ai.application.AiService;
import com.sogonsogon.gonggomoon.domain.ai.application.AiUsagePolicyService;
import com.sogonsogon.gonggomoon.domain.ai.domain.AiUsageType;
import com.sogonsogon.gonggomoon.domain.ai.dto.response.ExperienceExtractResponse;
import com.sogonsogon.gonggomoon.domain.ai.error.AiErrorCode;
import com.sogonsogon.gonggomoon.domain.experience.application.result.ExperienceExtractionResult;
import com.sogonsogon.gonggomoon.domain.experience.error.ExperienceErrorCode;
import com.sogonsogon.gonggomoon.domain.file.api.request.UploadFileRequest;
import com.sogonsogon.gonggomoon.domain.file.application.FileAssetService;
import com.sogonsogon.gonggomoon.domain.file.application.result.UploadFileResult;
import com.sogonsogon.gonggomoon.domain.file.domain.DocumentCategory;
import com.sogonsogon.gonggomoon.global.error.BaseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ExperienceExtractionServiceTest {

    private static final Long USER_ID = 1L;
    private static final Long FILE_ASSET_ID = 10L;
    private static final UUID EXTRACTION_ID = UUID.randomUUID();

    @Mock
    private AiService aiService;

    @Mock
    private FileAssetService fileAssetService;

    @Mock
    private AiUsagePolicyService aiUsagePolicyService;

    @Mock
    private MultipartFile file;

    @InjectMocks
    private ExperienceExtractionService experienceExtractionService;

    private final UploadFileRequest request = new UploadFileRequest(DocumentCategory.RESUME);

    @Nested
    @DisplayName("경험 추출 시작")
    class StartExperienceExtraction {

        @Test
        @DisplayName("파일을 임시 업로드하고 AI 경험 추출을 요청한 뒤 추출 작업 ID를 반환한다")
        void startExperienceExtraction_success() {
            // given
            ReflectionTestUtils.setField(experienceExtractionService, "weeklyLimitEnabled", true);

            when(aiUsagePolicyService.reserve(USER_ID, AiUsageType.EXPERIENCE_EXTRACTION))
                    .thenReturn(true);
            when(fileAssetService.uploadFile(USER_ID, request, file))
                    .thenReturn(new UploadFileResult(FILE_ASSET_ID));
            when(aiService.requestExperienceExtraction(USER_ID, List.of(FILE_ASSET_ID)))
                    .thenReturn(new ExperienceExtractResponse(List.of(EXTRACTION_ID)));

            // when
            ExperienceExtractionResult result =
                    experienceExtractionService.startExperienceExtraction(request, file, USER_ID);

            // then
            assertEquals(EXTRACTION_ID, result.extractionId());

            verify(aiUsagePolicyService).reserve(USER_ID, AiUsageType.EXPERIENCE_EXTRACTION);
            verify(fileAssetService).uploadFile(USER_ID, request, file);
            verify(aiService).requestExperienceExtraction(USER_ID, List.of(FILE_ASSET_ID));
        }

        @Test
        @DisplayName("이번 주 사용 가능 횟수를 초과하면 예외가 발생하고 파일 업로드/AI 요청은 수행되지 않는다")
        void startExperienceExtraction_fail_whenWeeklyLimitExceeded() {
            // given
            ReflectionTestUtils.setField(experienceExtractionService, "weeklyLimitEnabled", true);
            when(aiUsagePolicyService.reserve(USER_ID, AiUsageType.EXPERIENCE_EXTRACTION))
                    .thenReturn(false);

            // when
            BaseException exception = assertThrows(
                    BaseException.class,
                    () -> experienceExtractionService.startExperienceExtraction(request, file, USER_ID)
            );

            // then
            assertEquals(ExperienceErrorCode.WEEKLY_LIMIT_EXCEEDED, exception.getErrorCode());
            verify(fileAssetService, never()).uploadFile(any(), any(), any());
            verifyNoInteractions(aiService);
        }

        @Test
        @DisplayName("Cloud Tasks 발행에 실패하면 사용량을 환불하고 임시 파일을 정리한 뒤 예외를 전파한다")
        void startExperienceExtraction_fail_whenEnqueueFails_refundsAndDeletesTempFile() {
            // given
            ReflectionTestUtils.setField(experienceExtractionService, "weeklyLimitEnabled", true);
            when(aiUsagePolicyService.reserve(USER_ID, AiUsageType.EXPERIENCE_EXTRACTION))
                    .thenReturn(true);
            when(fileAssetService.uploadFile(USER_ID, request, file))
                    .thenReturn(new UploadFileResult(FILE_ASSET_ID));
            when(aiService.requestExperienceExtraction(USER_ID, List.of(FILE_ASSET_ID)))
                    .thenThrow(new BaseException(AiErrorCode.AI_SERVER_ERROR));

            // when
            assertThrows(
                    BaseException.class,
                    () -> experienceExtractionService.startExperienceExtraction(request, file, USER_ID)
            );

            // then
            verify(aiUsagePolicyService).refund(eq(USER_ID), eq(AiUsageType.EXPERIENCE_EXTRACTION), any());
            verify(fileAssetService).deleteTemporaryFiles(List.of(FILE_ASSET_ID));
        }
    }
}
