package com.sogonsogon.gonggomoon.domain.strategy.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sogonsogon.gonggomoon.domain.experience.domain.Experience;
import com.sogonsogon.gonggomoon.domain.experience.domain.ExperienceRepository;
import com.sogonsogon.gonggomoon.domain.experience.domain.ExperienceType;
import com.sogonsogon.gonggomoon.domain.industry.domain.Industry;
import com.sogonsogon.gonggomoon.domain.industry.domain.IndustryRepository;
import com.sogonsogon.gonggomoon.domain.strategy.api.request.GeneratePortfolioStrategyRequest;
import com.sogonsogon.gonggomoon.domain.strategy.application.result.GeneratePortfolioStrategyResult;
import com.sogonsogon.gonggomoon.domain.strategy.application.result.PortfolioStrategyDetailResult;
import com.sogonsogon.gonggomoon.domain.strategy.application.result.PortfolioStrategyListResult;
import com.sogonsogon.gonggomoon.domain.strategy.application.result.PortfolioStrategyListResultItem;
import com.sogonsogon.gonggomoon.domain.strategy.content.ExperienceOrderingItem;
import com.sogonsogon.gonggomoon.domain.strategy.content.ExperienceStrategyPoint;
import com.sogonsogon.gonggomoon.domain.strategy.content.ImprovementGuide;
import com.sogonsogon.gonggomoon.domain.strategy.content.PortfolioStrategyContent;
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
import java.util.Optional;

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
    private IndustryRepository industryRepository;

    @Mock
    private PortfolioStrategyContentGenerator portfolioStrategyContentGenerator;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private PortfolioStrategyService portfolioStrategyService;

    private static final Long USER_ID = 1L;

    private static final Long INDUSTRY_ID = 1L;
    private static final Long INDUSTRY_ID_MASTER = 2L;

    @Nested
    @DisplayName("generate")
    class GenerateTest {

        @Test
        @DisplayName("experienceIds가 null이면 EXPERIENCE_IDS_REQUIRED 예외가 발생한다")
        void generate_fail_whenExperienceIdsIsNull() {
            // given
            GeneratePortfolioStrategyRequest req = new GeneratePortfolioStrategyRequest(
                    JobType.BACKEND,
                    INDUSTRY_ID,
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
                    INDUSTRY_ID,
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
                    INDUSTRY_ID,
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
                    INDUSTRY_ID,
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
                    INDUSTRY_ID,
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
            PortfolioStrategyListResultItem strategy1 = createPortfolioStrategyListItem(
                    100L,
                    JobType.BACKEND,
                    "핀테크",
                    Instant.parse("2026-03-10T10:00:00Z")
            );

            PortfolioStrategyListResultItem strategy2 = createPortfolioStrategyListItem(
                    99L,
                    JobType.BACKEND,
                    "마스터",
                    Instant.parse("2026-03-09T10:00:00Z")
            );

            List<PortfolioStrategyListResultItem> items = List.of(strategy1, strategy2);

            when(portfolioStrategyRepository.findPortfolioStrategyListByUserId(USER_ID))
                    .thenReturn(items);

            // when
            PortfolioStrategyListResult result = portfolioStrategyService.getPortfolioStrategyList(USER_ID);

            // then
            assertNotNull(result);
            assertEquals(2, result.totalCount());
            assertEquals(2, result.contents().size());

            PortfolioStrategyListResultItem first = result.contents().get(0);
            assertEquals(100L, first.strategyId());
            assertEquals(JobType.BACKEND, first.jobType());
            assertEquals("핀테크", first.industryName());
            assertEquals(Instant.parse("2026-03-10T10:00:00Z"), first.createdAt());

            PortfolioStrategyListResultItem second = result.contents().get(1);
            assertEquals(99L, second.strategyId());
            assertEquals(JobType.BACKEND, second.jobType());
            assertEquals("마스터", second.industryName());
            assertEquals(Instant.parse("2026-03-09T10:00:00Z"), second.createdAt());

            verify(portfolioStrategyRepository).findPortfolioStrategyListByUserId(USER_ID);
        }

        @Test
        @DisplayName("조회된 포트폴리오 전략이 없으면 빈 목록을 반환한다")
        void getPortfolioStrategyList_empty() {
            // given
            when(portfolioStrategyRepository.findPortfolioStrategyListByUserId(USER_ID))
                    .thenReturn(List.of());

            // when
            PortfolioStrategyListResult result = portfolioStrategyService.getPortfolioStrategyList(USER_ID);

            // then
            assertNotNull(result);
            assertEquals(0, result.totalCount());
            assertNotNull(result.contents());
            assertTrue(result.contents().isEmpty());

            verify(portfolioStrategyRepository).findPortfolioStrategyListByUserId(USER_ID);
        }
    }

    @Nested
    @DisplayName("getDetail")
    class GetDetailListTest {

        @Test
        @DisplayName("전략 상세 조회에 성공하면 메타 정보와 결과 본문을 반환한다")
        void getPortfolioStrategyDetail_success() throws Exception {
            // given
            Long strategyId = 100L;
            Instant createdAt = Instant.parse("2026-03-10T10:00:00Z");

            PortfolioStrategy portfolioStrategy = createPortfolioStrategy(
                    strategyId,
                    USER_ID,
                    JobType.BACKEND,
                    INDUSTRY_ID,
                    createdAt
            );

            PortfolioStrategyContent content = PortfolioStrategyContent.of(
                    "대규모 트래픽 환경에서 안정성과 데이터 기반 의사결정을 설계하는 백엔드 개발자",
                    List.of(
                            new ExperienceStrategyPoint(
                                    ExperienceType.PROJECT,
                                    "대용량 영상 업로드 시스템",
                                    "청크 업로드, 재시도, 장애 복구를 중심으로 대용량 파일 처리 안정성을 개선한 경험으로 정리하세요."
                            )
                    ),
                    List.of(
                            new ExperienceOrderingItem(
                                    1,
                                    "대용량 영상 업로드 시스템",
                                    "대규모 트래픽 처리와 안정성 설계 역량을 가장 강하게 보여줄 수 있는 핵심 경험이기 때문입니다."
                            )
                    ),
                    List.of("트래픽 대응", "안정성"),
                    List.of("대용량 파일 업로드 처리", "장애 대응 및 복구 설계"),
                    List.of("업로드 실패율 감소 수치 제시"),
                    List.of(
                            new ImprovementGuide(
                                    "성과 수치 보완",
                                    "전후 비교가 가능한 수치를 함께 제시하면 설득력이 높아집니다."
                            )
                    )
            );

            Industry industry = mock(Industry.class);
            when(industry.getName()).thenReturn("핀테크");

            when(portfolioStrategyRepository.findByIdAndUserId(strategyId, USER_ID))
                    .thenReturn(Optional.of(portfolioStrategy));
            when(objectMapper.readValue(portfolioStrategy.getResultJson(), PortfolioStrategyContent.class))
                    .thenReturn(content);
            when(industryRepository.findById(INDUSTRY_ID))
                    .thenReturn(Optional.of(industry));

            // when
            PortfolioStrategyDetailResult result =
                    portfolioStrategyService.getPortfolioStrategyDetail(strategyId, USER_ID);

            // then
            assertNotNull(result);
            assertEquals(strategyId, result.strategyId());
            assertEquals(JobType.BACKEND, result.jobType());
            assertEquals("핀테크", result.industryName());
            assertEquals(1, result.selectedExperienceCount());
            assertEquals(createdAt, result.createdAt());

            assertEquals("대규모 트래픽 환경에서 안정성과 데이터 기반 의사결정을 설계하는 백엔드 개발자",
                    result.mainPositioningMessage());

            assertNotNull(result.experienceStrategyPoints());
            assertEquals(1, result.experienceStrategyPoints().size());
            assertEquals(ExperienceType.PROJECT, result.experienceStrategyPoints().get(0).experienceType());
            assertEquals("대용량 영상 업로드 시스템", result.experienceStrategyPoints().get(0).experienceTitle());

            assertNotNull(result.experienceOrdering());
            assertEquals(1, result.experienceOrdering().size());
            assertEquals(1, result.experienceOrdering().get(0).order());
            assertEquals("대용량 영상 업로드 시스템", result.experienceOrdering().get(0).title());

            assertEquals(List.of("트래픽 대응", "안정성"), result.keywords());
            assertEquals(List.of("대용량 파일 업로드 처리", "장애 대응 및 복구 설계"), result.strengths());
            assertEquals(List.of("업로드 실패율 감소 수치 제시"), result.kpiCheckList());

            assertNotNull(result.improvementGuides());
            assertEquals(1, result.improvementGuides().size());
            assertEquals("성과 수치 보완", result.improvementGuides().get(0).title());

            verify(portfolioStrategyRepository).findByIdAndUserId(strategyId, USER_ID);
            verify(objectMapper).readValue(portfolioStrategy.getResultJson(), PortfolioStrategyContent.class);
        }

        @Test
        @DisplayName("해당 사용자의 전략이 없으면 NOT_FOUND 예외가 발생한다")
        void getPortfolioStrategyDetail_fail_whenStrategyNotFound() {
            // given
            Long strategyId = 100L;

            when(portfolioStrategyRepository.findByIdAndUserId(strategyId, USER_ID))
                    .thenReturn(Optional.empty());

            // when
            BaseException exception = assertThrows(
                    BaseException.class,
                    () -> portfolioStrategyService.getPortfolioStrategyDetail(strategyId, USER_ID)
            );

            // then
            assertEquals(PortfolioStrategyErrorCode.NOT_FOUND, exception.getErrorCode());
            verify(portfolioStrategyRepository).findByIdAndUserId(strategyId, USER_ID);
            verifyNoInteractions(objectMapper);
        }

        @Test
        @DisplayName("전략 결과 JSON 역직렬화에 실패하면 RESULT_JSON_DESERIALIZATION_FAILED 예외가 발생한다")
        void getPortfolioStrategyDetail_fail_whenJsonDeserializationFails() throws Exception {
            // given
            Long strategyId = 100L;

            PortfolioStrategy portfolioStrategy = createPortfolioStrategy(
                    strategyId,
                    USER_ID,
                    JobType.BACKEND,
                    INDUSTRY_ID,
                    Instant.parse("2026-03-10T10:00:00Z")
            );

            when(portfolioStrategyRepository.findByIdAndUserId(strategyId, USER_ID))
                    .thenReturn(Optional.of(portfolioStrategy));
            when(objectMapper.readValue(portfolioStrategy.getResultJson(), PortfolioStrategyContent.class))
                    .thenThrow(new JsonProcessingException("deserialization failed") {});

            // when
            BaseException exception = assertThrows(
                    BaseException.class,
                    () -> portfolioStrategyService.getPortfolioStrategyDetail(strategyId, USER_ID)
            );

            // then
            assertEquals(PortfolioStrategyErrorCode.RESULT_JSON_DESERIALIZATION_FAILED, exception.getErrorCode());
            verify(portfolioStrategyRepository).findByIdAndUserId(strategyId, USER_ID);
            verify(objectMapper).readValue(portfolioStrategy.getResultJson(), PortfolioStrategyContent.class);
        }

    }

    @Nested
    @DisplayName("delete")
    class DeleteTest {

        @Test
        @DisplayName("해당 사용자의 포트폴리오 전략이 존재하면 삭제한다")
        void deletePortfolioStrategy_success() throws Exception {
            // given
            Long strategyId = 100L;

            PortfolioStrategy portfolioStrategy = createPortfolioStrategy(
                    strategyId,
                    USER_ID,
                    JobType.BACKEND,
                    INDUSTRY_ID,
                    Instant.parse("2026-03-10T10:00:00Z")
            );

            when(portfolioStrategyRepository.findByIdAndUserId(strategyId, USER_ID))
                    .thenReturn(Optional.of(portfolioStrategy));

            // when
            portfolioStrategyService.deletePortfolioStrategy(strategyId, USER_ID);

            // then
            verify(portfolioStrategyRepository).findByIdAndUserId(strategyId, USER_ID);
            verify(portfolioStrategyRepository).delete(portfolioStrategy);
        }

        @Test
        @DisplayName("해당 사용자의 포트폴리오 전략이 없으면 NOT_FOUND 예외가 발생한다")
        void deletePortfolioStrategy_fail_whenStrategyNotFound() {
            // given
            Long strategyId = 100L;

            when(portfolioStrategyRepository.findByIdAndUserId(strategyId, USER_ID))
                    .thenReturn(Optional.empty());

            // when
            BaseException exception = assertThrows(
                    BaseException.class,
                    () -> portfolioStrategyService.deletePortfolioStrategy(strategyId, USER_ID)
            );

            // then
            assertEquals(PortfolioStrategyErrorCode.NOT_FOUND, exception.getErrorCode());
            verify(portfolioStrategyRepository).findByIdAndUserId(strategyId, USER_ID);
            verify(portfolioStrategyRepository, never()).delete(any(PortfolioStrategy.class));
        }
    }

    private PortfolioStrategyListResultItem createPortfolioStrategyListItem(
            Long id,
            JobType jobType,
            String industryName,
            Instant createdAt
    ) {
        return PortfolioStrategyListResultItem.builder()
                .strategyId(id)
                .jobType(jobType)
                .industryName(industryName)
                .createdAt(createdAt)
                .build();
    }

    private PortfolioStrategy createPortfolioStrategy(
            Long id,
            Long userId,
            JobType jobType,
            Long industryId,
            Instant createdAt
    ) throws Exception {
        PortfolioStrategy strategy = PortfolioStrategy.create(
                userId,
                jobType,
                industryId,
                "{}",
                1
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