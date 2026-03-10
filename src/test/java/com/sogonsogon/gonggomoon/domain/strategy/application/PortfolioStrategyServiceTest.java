package com.sogonsogon.gonggomoon.domain.strategy.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sogonsogon.gonggomoon.domain.experience.domain.Experience;
import com.sogonsogon.gonggomoon.domain.experience.domain.ExperienceRepository;
import com.sogonsogon.gonggomoon.domain.experience.domain.ExperienceType;
import com.sogonsogon.gonggomoon.domain.strategy.api.request.GeneratePortfolioStrategyRequest;
import com.sogonsogon.gonggomoon.domain.strategy.application.result.GeneratePortfolioStrategyResult;
import com.sogonsogon.gonggomoon.domain.strategy.application.result.PortfolioStrategyListResult;
import com.sogonsogon.gonggomoon.domain.strategy.application.result.PortfolioStrategyListResultItem;
import com.sogonsogon.gonggomoon.domain.strategy.content.PortfolioStrategyContent;
import com.sogonsogon.gonggomoon.domain.strategy.domain.IndustryType;
import com.sogonsogon.gonggomoon.domain.strategy.domain.JobType;
import com.sogonsogon.gonggomoon.domain.strategy.domain.PortfolioStrategy;
import com.sogonsogon.gonggomoon.domain.strategy.domain.PortfolioStrategyRepository;
import com.sogonsogon.gonggomoon.domain.strategy.error.PortfolioStrategyErrorCode;
import com.sogonsogon.gonggomoon.domain.strategy.generator.PortfolioStrategyContentGenerator;
import com.sogonsogon.gonggomoon.global.error.BaseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PortfolioStrategyServiceTest {

    @Mock
    private PortfolioStrategyRepository portfolioStrategyRepository;

    @Mock
    private ExperienceRepository experienceRepository;

    @Mock
    private PortfolioStrategyContentGenerator portfolioStrategyContentGenerator;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private PortfolioStrategyService portfolioStrategyService;

    private static final Long USER_ID = 1L;

    @Nested
    @DisplayName("generate")
    class GenerateTest {

        @Test
        @DisplayName("experienceIds가 null이면 EXPERIENCE_IDS_REQUIRED 예외가 발생한다")
        void generate_fail_whenExperienceIdsIsNull() {
            // given
            GeneratePortfolioStrategyRequest req = new GeneratePortfolioStrategyRequest(
                    JobType.BACKEND,
                    IndustryType.FINTECH_FINANCIAL,
                    null
            );

            // when
            BaseException exception = assertThrows(
                    BaseException.class,
                    () -> portfolioStrategyService.generate(USER_ID, req)
            );

            // then
            assertEquals(PortfolioStrategyErrorCode.EXPERIENCE_IDS_REQUIRED, exception.getErrorCode());
            verifyNoInteractions(experienceRepository, portfolioStrategyContentGenerator, objectMapper, portfolioStrategyRepository);
        }

        @Test
        @DisplayName("experienceIds가 비어 있으면 EXPERIENCE_IDS_REQUIRED 예외가 발생한다")
        void generate_fail_whenExperienceIdsIsEmpty() {
            // given
            GeneratePortfolioStrategyRequest req = new GeneratePortfolioStrategyRequest(
                    JobType.BACKEND,
                    IndustryType.FINTECH_FINANCIAL,
                    List.of()
            );

            // when
            BaseException exception = assertThrows(
                    BaseException.class,
                    () -> portfolioStrategyService.generate(USER_ID, req)
            );

            // then
            assertEquals(PortfolioStrategyErrorCode.EXPERIENCE_IDS_REQUIRED, exception.getErrorCode());
            verifyNoInteractions(experienceRepository, portfolioStrategyContentGenerator, objectMapper, portfolioStrategyRepository);
        }

        @Test
        @DisplayName("선택한 경험 중 일부를 찾을 수 없으면 REQUESTED_EXPERIENCE_NOT_FOUND 예외가 발생한다")
        void generate_fail_whenSomeRequestedExperiencesAreNotFound() {
            // given
            GeneratePortfolioStrategyRequest req = new GeneratePortfolioStrategyRequest(
                    JobType.BACKEND,
                    IndustryType.FINTECH_FINANCIAL,
                    List.of(1L, 2L)
            );

            Experience experience = createExperience(USER_ID, "캡스톤 프로젝트");

            when(experienceRepository.findAllByIdInAndUserId(req.experienceIds(), USER_ID))
                    .thenReturn(List.of(experience)); // 2개 요청했지만 1개만 조회됨

            // when
            BaseException exception = assertThrows(
                    BaseException.class,
                    () -> portfolioStrategyService.generate(USER_ID, req)
            );

            // then
            assertEquals(PortfolioStrategyErrorCode.REQUESTED_EXPERIENCE_NOT_FOUND, exception.getErrorCode());
            verify(experienceRepository).findAllByIdInAndUserId(req.experienceIds(), USER_ID);
            verifyNoInteractions(portfolioStrategyContentGenerator, objectMapper, portfolioStrategyRepository);
        }

        @Test
        @DisplayName("정상 요청이면 전략을 생성하고 저장한다")
        void generate_success() throws Exception {
            // given
            GeneratePortfolioStrategyRequest req = new GeneratePortfolioStrategyRequest(
                    JobType.BACKEND,
                    IndustryType.FINTECH_FINANCIAL,
                    List.of(1L, 2L)
            );

            Experience experience1 = createExperience(USER_ID, "캡스톤 프로젝트");
            Experience experience2 = createExperience(USER_ID, "인턴 경험");
            List<Experience> experiences = List.of(experience1, experience2);

            PortfolioStrategyContent content = PortfolioStrategyContent.of(
                    "대규모 트래픽 환경에서 안정성과 데이터 기반 의사결정을 설계하는 백엔드 개발자",
                    List.of(),
                    List.of(),
                    List.of("안정성", "트래픽 대응"),
                    List.of("문제 해결", "데이터 기반 사고"),
                    List.of("전환율 개선", "운영 효율성"),
                    List.of()
            );

            when(experienceRepository.findAllByIdInAndUserId(req.experienceIds(), USER_ID))
                    .thenReturn(experiences);
            when(portfolioStrategyContentGenerator.generate(experiences, req))
                    .thenReturn(content);
            when(objectMapper.writeValueAsString(content))
                    .thenReturn("{}");

            // when
            GeneratePortfolioStrategyResult result = portfolioStrategyService.generate(USER_ID, req);

            // then
            assertNotNull(result);
            verify(experienceRepository).findAllByIdInAndUserId(req.experienceIds(), USER_ID);
            verify(portfolioStrategyContentGenerator).generate(experiences, req);
            verify(objectMapper).writeValueAsString(content);
            verify(portfolioStrategyRepository).save(any(PortfolioStrategy.class));
        }

        @Test
        @DisplayName("전략 결과 JSON 직렬화에 실패하면 RESULT_JSON_SERIALIZATION_FAILED 예외가 발생한다")
        void generate_fail_whenJsonSerializationFails() throws Exception {
            // given
            GeneratePortfolioStrategyRequest req = new GeneratePortfolioStrategyRequest(
                    JobType.BACKEND,
                    IndustryType.FINTECH_FINANCIAL,
                    List.of(1L)
            );

            Experience experience = createExperience(USER_ID, "캡스톤 프로젝트");
            List<Experience> experiences = List.of(experience);

            PortfolioStrategyContent content = PortfolioStrategyContent.of(
                    "포지셔닝 메시지",
                    List.of(),
                    List.of(),
                    List.of("키워드"),
                    List.of("강점"),
                    List.of("KPI"),
                    List.of()
            );

            when(experienceRepository.findAllByIdInAndUserId(req.experienceIds(), USER_ID))
                    .thenReturn(experiences);
            when(portfolioStrategyContentGenerator.generate(experiences, req))
                    .thenReturn(content);
            when(objectMapper.writeValueAsString(content))
                    .thenThrow(new JsonProcessingException("serialization failed") {});

            // when
            BaseException exception = assertThrows(
                    BaseException.class,
                    () -> portfolioStrategyService.generate(USER_ID, req)
            );

            // then
            assertEquals(PortfolioStrategyErrorCode.RESULT_JSON_SERIALIZATION_FAILED, exception.getErrorCode());
            verify(experienceRepository).findAllByIdInAndUserId(req.experienceIds(), USER_ID);
            verify(portfolioStrategyContentGenerator).generate(experiences, req);
            verify(objectMapper).writeValueAsString(content);
            verify(portfolioStrategyRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("getList")
    class GetListTest {

        @Test
        @DisplayName("사용자의 포트폴리오 전략 목록을 조회하면 totalCount와 contents를 반환한다")
        void getPortfolioStrategyList_success() throws Exception {
            // given
            PortfolioStrategy strategy1 = createPortfolioStrategy(
                    100L,
                    USER_ID,
                    JobType.BACKEND,
                    IndustryType.AI,
                    Instant.parse("2026-03-10T10:00:00Z")
            );

            PortfolioStrategy strategy2 = createPortfolioStrategy(
                    99L,
                    USER_ID,
                    JobType.BACKEND,
                    IndustryType.MASTER,
                    Instant.parse("2026-03-09T10:00:00Z")
            );

            List<PortfolioStrategy> strategies = List.of(strategy1, strategy2);

            when(portfolioStrategyRepository.findAllByUserIdOrderByCreatedAtDesc(USER_ID))
                    .thenReturn(strategies);

            // when
            PortfolioStrategyListResult result = portfolioStrategyService.getPortfolioStrategyList(USER_ID);

            // then
            assertNotNull(result);
            assertEquals(2, result.totalCount());
            assertEquals(2, result.contents().size());

            PortfolioStrategyListResultItem first = result.contents().get(0);
            assertEquals(100L, first.strategyId());
            assertEquals(JobType.BACKEND, first.jobType());
            assertEquals(IndustryType.AI, first.industryType());
            assertEquals(Instant.parse("2026-03-10T10:00:00Z"), first.createdAt());

            PortfolioStrategyListResultItem second = result.contents().get(1);
            assertEquals(99L, second.strategyId());
            assertEquals(JobType.BACKEND, second.jobType());
            assertEquals(IndustryType.MASTER, second.industryType());
            assertEquals(Instant.parse("2026-03-09T10:00:00Z"), second.createdAt());

            verify(portfolioStrategyRepository).findAllByUserIdOrderByCreatedAtDesc(USER_ID);
        }

        @Test
        @DisplayName("조회된 포트폴리오 전략이 없으면 빈 목록을 반환한다")
        void getPortfolioStrategyList_empty() {
            // given
            when(portfolioStrategyRepository.findAllByUserIdOrderByCreatedAtDesc(USER_ID))
                    .thenReturn(List.of());

            // when
            PortfolioStrategyListResult result = portfolioStrategyService.getPortfolioStrategyList(USER_ID);

            // then
            assertNotNull(result);
            assertEquals(0, result.totalCount());
            assertNotNull(result.contents());
            assertTrue(result.contents().isEmpty());

            verify(portfolioStrategyRepository).findAllByUserIdOrderByCreatedAtDesc(USER_ID);
        }
    }

    private PortfolioStrategy createPortfolioStrategy(
            Long id,
            Long userId,
            JobType jobType,
            IndustryType industryType,
            Instant createdAt
    ) throws Exception {
        PortfolioStrategy strategy = PortfolioStrategy.create(
                userId,
                jobType,
                industryType,
                "{}"
        );

        setField(strategy, "id", id);
        setField(strategy, "createdAt", createdAt);

        return strategy;
    }

    private Experience createExperience(Long userId, String title) {
        return Experience.create(
                userId,
                title,
                ExperienceType.PROJECT,
                "경험 내용",
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 2, 1)
        );
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}