package com.sogonsogon.gonggomoon.domain.ai.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sogonsogon.gonggomoon.domain.ai.domain.AiJobStatus;
import com.sogonsogon.gonggomoon.domain.ai.domain.ExperienceItem;
import com.sogonsogon.gonggomoon.domain.ai.domain.Experiences;
import com.sogonsogon.gonggomoon.domain.ai.domain.ExtractedExperience;
import com.sogonsogon.gonggomoon.domain.ai.domain.ExtractedExperienceRepository;
import com.sogonsogon.gonggomoon.domain.ai.domain.ExtractionStatus;
import com.sogonsogon.gonggomoon.domain.ai.dto.request.BaseCallbackRequest;
import com.sogonsogon.gonggomoon.domain.ai.infrastructure.ExperienceResultMapper;
import com.sogonsogon.gonggomoon.domain.ai.infrastructure.InterviewQuestionResultMapper;
import com.sogonsogon.gonggomoon.domain.experience.domain.Experience;
import com.sogonsogon.gonggomoon.domain.experience.domain.ExperienceRepository;
import com.sogonsogon.gonggomoon.domain.experience.domain.ExperienceType;
import com.sogonsogon.gonggomoon.domain.interviewStrategy.domain.InterviewStrategyRepository;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.domain.JobType;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.domain.PortfolioStrategy;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.domain.PortfolioStrategyRepository;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.error.PortfolioStrategyErrorCode;
import com.sogonsogon.gonggomoon.global.error.BaseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AiCallbackServiceTest {

    @Mock
    private ExperienceResultMapper experienceResultMapper;

    @Mock
    private ExtractedExperienceRepository extractedExperienceRepository;

    @Mock
    private ExperienceRepository experienceRepository;

    @Mock
    private PortfolioStrategyRepository portfolioStrategyRepository;

    @Mock
    private InterviewStrategyRepository interviewStrategyRepository;

    @Mock
    private InterviewQuestionResultMapper interviewQuestionResultMapper;

    @Mock
    private AiUsagePolicyService aiUsagePolicyService;

    @Mock
    private AiJobSseService aiJobSseService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private AiCallbackService aiCallbackService;

    private static final Long USER_ID = 1L;

    private static final Long INDUSTRY_ID = 1L;

    private static final Long STRATEGY_ID = 1L;

    @Nested
    @DisplayName("request")
    class requestTest {
        @Test
        @DisplayName("경험 추출 콜백 성공 시 추출 결과를 experience 테이블에 바로 저장한다")
        void createExtractedExperience_success_saveExperienceDirectly() throws Exception {
            // given
            Long extractedExperienceId = 1L;
            JsonNode resultNode = new ObjectMapper().readTree("""
                    [
                      {
                        "extracted_experience_id": 1,
                        "result": {
                          "analysis": {
                            "experiences": [
                              {
                                "title": "캡스톤 프로젝트",
                                "experienceContent": "백엔드 API 개발",
                                "experienceType": "PROJECT",
                                "startDate": "2024-03",
                                "endDate": "2024-06"
                              }
                            ]
                          }
                        }
                      }
                    ]
                    """);
            BaseCallbackRequest request = new BaseCallbackRequest(
                    "EXTRACT_EXPERIENCE",
                    extractedExperienceId,
                    USER_ID,
                    AiJobStatus.COMPLETED,
                    resultNode,
                    null,
                    1,
                    Instant.now()
            );
            ExtractedExperience extractedExperience =
                    ExtractedExperience.create(USER_ID, 10L, LocalDate.now());
            ReflectionTestUtils.setField(extractedExperience, "id", extractedExperienceId);
            Experiences experiences = Experiences.of(List.of(
                    ExperienceItem.builder()
                            .title("캡스톤 프로젝트")
                            .experienceType(ExperienceType.PROJECT)
                            .experienceContent("백엔드 API 개발")
                            .startDate(YearMonth.of(2024, 3))
                            .endDate(YearMonth.of(2024, 6))
                            .build()
            ));

            when(extractedExperienceRepository.findAllById(List.of(extractedExperienceId)))
                    .thenReturn(List.of(extractedExperience));
            when(experienceResultMapper.toExperiencesFromCallbackItem(any(JsonNode.class)))
                    .thenReturn(experiences);

            // when
            aiCallbackService.createExtractedExperience(request);

            // then
            ArgumentCaptor<Iterable<Experience>> captor = ArgumentCaptor.forClass(Iterable.class);
            verify(experienceRepository).saveAll(captor.capture());
            List<Experience> savedExperiences = StreamSupport
                    .stream(captor.getValue().spliterator(), false)
                    .toList();

            assertEquals(1, savedExperiences.size());
            Experience savedExperience = savedExperiences.get(0);
            assertEquals(USER_ID, savedExperience.getUserId());
            assertEquals("캡스톤 프로젝트", savedExperience.getTitle());
            assertEquals(ExperienceType.PROJECT, savedExperience.getExperienceType());
            assertEquals("백엔드 API 개발", savedExperience.getExperienceContent());
            assertEquals(LocalDate.of(2024, 3, 1), savedExperience.getStartDate());
            assertEquals(LocalDate.of(2024, 6, 30), savedExperience.getEndDate());
            assertEquals(ExtractionStatus.READY, extractedExperience.getStatus());
            assertNull(extractedExperience.getExperiences());
            verify(extractedExperienceRepository).saveAll(List.of(extractedExperience));
        }

        @Test
        @DisplayName("전략 결과 JSON 직렬화에 실패하면 RESULT_JSON_SERIALIZATION_FAILED 예외가 발생한다")
        void updatePortfolioStrategy_fail_whenResultJsonSerializationFails() throws Exception {
            // given
            BaseCallbackRequest request = mock(BaseCallbackRequest.class);

            PortfolioStrategy strategy = PortfolioStrategy.create(
                    USER_ID,
                    JobType.BACKEND,
                    INDUSTRY_ID,
                    null,
                    2,
                    Instant.now(),
                    LocalDate.now(ZoneId.of("Asia/Seoul"))
            );

            Map<String, Object> result = new HashMap<>();
            result.put("portfolioStrategy", Map.of("title", "백엔드 전략", "summary", "요약"));

            JsonNode resultNode = new ObjectMapper().valueToTree(result);

            when(request.result()).thenReturn(resultNode);

            when(request.id()).thenReturn(STRATEGY_ID);
            when(request.userId()).thenReturn(USER_ID);

            when(portfolioStrategyRepository.findByIdAndUserId(STRATEGY_ID, USER_ID))
                    .thenReturn(Optional.of(strategy));

            when(objectMapper.writeValueAsString(any()))
                    .thenThrow(new JsonProcessingException("serialization failed") {});

            // when
            BaseException exception = assertThrows(
                    BaseException.class,
                    () -> aiCallbackService.updatePortfolioStrategy(request)
            );

            // then
            assertEquals(
                    PortfolioStrategyErrorCode.RESULT_JSON_SERIALIZATION_FAILED,
                    exception.getErrorCode()
            );

            verify(portfolioStrategyRepository).findByIdAndUserId(STRATEGY_ID, USER_ID);
            verify(objectMapper).writeValueAsString(any());
            verify(portfolioStrategyRepository, never()).save(any());
        }
    }
}
