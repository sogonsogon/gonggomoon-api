package com.sogonsogon.gonggomoon.domain.experience.application;

import com.sogonsogon.gonggomoon.domain.ai.application.AiService;
import com.sogonsogon.gonggomoon.domain.ai.dto.response.ExperienceExtractResponse;
import com.sogonsogon.gonggomoon.domain.experience.api.request.ExperienceExtractRequest;
import com.sogonsogon.gonggomoon.domain.experience.application.result.ExperienceExtractionResult;
import com.sogonsogon.gonggomoon.domain.experience.domain.FileAsset;
import com.sogonsogon.gonggomoon.domain.experience.domain.FileAssetRepository;
import com.sogonsogon.gonggomoon.domain.experience.error.ExperienceErrorCode;
import com.sogonsogon.gonggomoon.global.error.BaseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class ExperienceExtractionServiceTest {

    private static final Long USER_ID = 1L;
    private static final Long FILE_ASSET_ID_1 = 10L;
    private static final Long FILE_ASSET_ID_2 = 11L;
    private static final List<Long> EXTRACTED_EXPERIENCE_IDS = List.of(100L, 101L);

    @Mock
    private AiService aiService;

    @Mock
    private FileAssetRepository fileAssetRepository;

    @InjectMocks
    private ExperienceExtractionService experienceExtractionService;

    @Nested
    @DisplayName("경험 추출 시작")
    class StartExperienceExtraction {

        @Test
        @DisplayName("사용자 소유 파일들이 모두 존재하면 AI 경험 추출을 요청하고 extractedExperienceId를 반환한다")
        void startExperienceExtraction_success() {
            // given
            ExperienceExtractRequest request =
                    new ExperienceExtractRequest(List.of(FILE_ASSET_ID_1, FILE_ASSET_ID_2));

            FileAsset fileAsset1 = mock(FileAsset.class);
            FileAsset fileAsset2 = mock(FileAsset.class);

            when(fileAssetRepository.findAllByIdInAndUserId(request.fileAssetIds(), USER_ID))
                    .thenReturn(List.of(fileAsset1, fileAsset2));

            when(aiService.requestExperienceExtraction(USER_ID, request.fileAssetIds()))
                    .thenReturn(new ExperienceExtractResponse(EXTRACTED_EXPERIENCE_IDS));

            // when
            ExperienceExtractionResult result =
                    experienceExtractionService.startExperienceExtraction(request, USER_ID);

            // then
            assertEquals(EXTRACTED_EXPERIENCE_IDS, result.extractedExperienceIds());

            verify(fileAssetRepository).findAllByIdInAndUserId(request.fileAssetIds(), USER_ID);
            verify(aiService).requestExperienceExtraction(USER_ID, request.fileAssetIds());
        }


        @Test
        @DisplayName("요청한 파일 중 일부가 존재하지 않거나 본인 소유가 아니면 예외가 발생하고 AI 서비스는 호출되지 않는다")
        void startExperienceExtraction_fail_whenInvalidFileAssetRequest() {
            // given
            ExperienceExtractRequest request =
                    new ExperienceExtractRequest(List.of(FILE_ASSET_ID_1, FILE_ASSET_ID_2));

            when(fileAssetRepository.findAllByIdInAndUserId(request.fileAssetIds(), USER_ID))
                    .thenReturn(List.of(mock(FileAsset.class))); // 1개만 조회됨

            // when
            BaseException exception = assertThrows(
                    BaseException.class,
                    () -> experienceExtractionService.startExperienceExtraction(request, USER_ID)
            );

            // then
            assertEquals(ExperienceErrorCode.INVALID_FILE_ASSET_REQUEST, exception.getErrorCode());
            verify(fileAssetRepository).findAllByIdInAndUserId(request.fileAssetIds(), USER_ID);
            verifyNoInteractions(aiService);
        }
    }
}
