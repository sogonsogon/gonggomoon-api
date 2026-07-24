package com.sogonsogon.gonggomoon.domain.portfolioStrategy.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sogonsogon.gonggomoon.domain.ai.application.AiUsagePolicyService;
import com.sogonsogon.gonggomoon.domain.ai.domain.AiUsageType;
import com.sogonsogon.gonggomoon.domain.ai.domain.PostAnalysis;
import com.sogonsogon.gonggomoon.domain.ai.domain.PostAnalysisRepository;
import com.sogonsogon.gonggomoon.domain.experience.domain.Experience;
import com.sogonsogon.gonggomoon.domain.experience.domain.ExperienceRepository;
import com.sogonsogon.gonggomoon.domain.experience.domain.ExperienceType;
import com.sogonsogon.gonggomoon.domain.industry.domain.Industry;
import com.sogonsogon.gonggomoon.domain.industry.domain.IndustryRepository;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.api.request.GeneratePortfolioStrategyRequest;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.application.result.GeneratePortfolioStrategyResult;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.application.result.PortfolioStrategyDetailQueryResult;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.application.result.PortfolioStrategyDetailResult;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.application.result.PortfolioStrategyListResult;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.application.result.PortfolioStrategyListResultItem;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.application.result.PortfolioStrategyListQueryItem;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.application.support.PortfolioStrategyCursor;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.content.ExperienceOrderingItem;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.content.ExperienceStrategyPoint;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.content.ImprovementGuide;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.content.PortfolioStrategyContent;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.domain.JobType;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.domain.PortfolioStrategy;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.domain.PortfolioStrategyGenerateStatus;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.domain.PortfolioStrategyRepository;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.error.PortfolioStrategyErrorCode;
import com.sogonsogon.gonggomoon.domain.portfolioStrategy.generator.PortfolioStrategyContentGenerator;
import com.sogonsogon.gonggomoon.global.error.BaseException;
import com.sogonsogon.gonggomoon.global.error.GlobalErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    private PostAnalysisRepository postAnalysisRepository;

    @Mock
    private IndustryRepository industryRepository;

    @Mock
    private PortfolioStrategyContentGenerator portfolioStrategyContentGenerator;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private AiUsagePolicyService aiUsagePolicyService;

    @InjectMocks
    private PortfolioStrategyService portfolioStrategyService;

    @BeforeEach
    void setUp() throws Exception {
        setField(portfolioStrategyService, "weeklyLimitEnabled", true);
        PostAnalysis postAnalysis = PostAnalysis.create("https://example.com/job", "공고", "요약");
        ReflectionTestUtils.setField(postAnalysis, "id", POST_ANALYSIS_ID);
        ReflectionTestUtils.setField(postAnalysis, "publicId", POST_ANALYSIS_PUBLIC_ID);
        lenient().when(postAnalysisRepository.findByPublicId(POST_ANALYSIS_PUBLIC_ID))
                .thenReturn(Optional.of(postAnalysis));
    }

    private static final Long USER_ID = 1L;

    private static final Long POST_ID = 20L;

    private static final Long INDUSTRY_ID = 1L;

    private static final Long POST_ANALYSIS_ID = 10L;

    private static final UUID POST_ANALYSIS_PUBLIC_ID = UUID.randomUUID();

    private static final UUID EXPERIENCE_PUBLIC_ID_1 = UUID.randomUUID();

    private static final UUID EXPERIENCE_PUBLIC_ID_2 = UUID.randomUUID();

    @Nested
    @DisplayName("createDraft")
    class CreateDraftTest {

        @Test
        @DisplayName("공고 분석 결과 ID로 DRAFT 전략을 생성한다")
        void createDraft_success() {
            // given
            PortfolioStrategy savedDraft = PortfolioStrategy.createDraft(
                    USER_ID,
                    POST_ID,
                    POST_ANALYSIS_ID,
                    Instant.now(),
                    LocalDate.now(ZoneId.of("Asia/Seoul"))
            );
            ReflectionTestUtils.setField(savedDraft, "id", 100L);

            when(portfolioStrategyRepository.save(any(PortfolioStrategy.class)))
                    .thenReturn(savedDraft);

            // when
            Long draftId = portfolioStrategyService.createDraft(USER_ID, POST_ID, POST_ANALYSIS_ID);

            // then
            assertEquals(100L, draftId);
            verify(portfolioStrategyRepository).save(any(PortfolioStrategy.class));
        }
    }

    @Nested
    @DisplayName("generate")
    class GenerateTest {

        @Test
        @DisplayName("experienceIds가 null이면 EXPERIENCE_IDS_REQUIRED 예외가 발생한다")
        void generate_fail_whenExperienceIdsIsNull() {
            // given
            GeneratePortfolioStrategyRequest req = new GeneratePortfolioStrategyRequest(
                    POST_ANALYSIS_PUBLIC_ID,
                    null
            );

            // when
            BaseException exception = assertThrows(
                    BaseException.class,
                    () -> portfolioStrategyService.generate(USER_ID, req)
            );

            // then
            assertEquals(PortfolioStrategyErrorCode.EXPERIENCE_IDS_REQUIRED, exception.getErrorCode());
            verifyNoInteractions(experienceRepository, portfolioStrategyContentGenerator, portfolioStrategyRepository);
        }

        @Test
        @DisplayName("experienceIds가 비어 있으면 EXPERIENCE_IDS_REQUIRED 예외가 발생한다")
        void generate_fail_whenExperienceIdsIsEmpty() {
            // given
            GeneratePortfolioStrategyRequest req = new GeneratePortfolioStrategyRequest(
                    POST_ANALYSIS_PUBLIC_ID,
                    List.of()
            );

            // when
            BaseException exception = assertThrows(
                    BaseException.class,
                    () -> portfolioStrategyService.generate(USER_ID, req)
            );

            // then
            assertEquals(PortfolioStrategyErrorCode.EXPERIENCE_IDS_REQUIRED, exception.getErrorCode());
            verifyNoInteractions(experienceRepository, portfolioStrategyContentGenerator, portfolioStrategyRepository);
        }

        @Test
        @DisplayName("선택한 경험 중 일부를 찾을 수 없으면 REQUESTED_EXPERIENCE_NOT_FOUND 예외가 발생한다")
        void generate_fail_whenSomeRequestedExperiencesAreNotFound() {
            // given
            GeneratePortfolioStrategyRequest req = new GeneratePortfolioStrategyRequest(
                    POST_ANALYSIS_PUBLIC_ID,
                    List.of(EXPERIENCE_PUBLIC_ID_1, EXPERIENCE_PUBLIC_ID_2)
            );

            Experience experience = createExperience(USER_ID, "캡스톤 프로젝트");

            when(experienceRepository.findAllByPublicIdInAndUserId(req.experienceIds(), USER_ID))
                    .thenReturn(List.of(experience)); // 2개 요청했지만 1개만 조회됨

            // when
            BaseException exception = assertThrows(
                    BaseException.class,
                    () -> portfolioStrategyService.generate(USER_ID, req)
            );

            // then
            assertEquals(PortfolioStrategyErrorCode.REQUESTED_EXPERIENCE_NOT_FOUND, exception.getErrorCode());
            verify(experienceRepository).findAllByPublicIdInAndUserId(req.experienceIds(), USER_ID);
            verifyNoInteractions(portfolioStrategyContentGenerator);
            verifyNoInteractions(aiUsagePolicyService);
            verify(portfolioStrategyRepository, never()).save(any(PortfolioStrategy.class));
        }

        @Test
        @DisplayName("정상 요청이면 draft를 PROCESSING으로 전환하고 AI 생성 요청을 보낸다")
        void generate_success() {
            // given
            GeneratePortfolioStrategyRequest req = new GeneratePortfolioStrategyRequest(
                    POST_ANALYSIS_PUBLIC_ID,
                    List.of(EXPERIENCE_PUBLIC_ID_1, EXPERIENCE_PUBLIC_ID_2)
            );

            Experience experience1 = createExperience(USER_ID, "캡스톤 프로젝트");
            Experience experience2 = createExperience(USER_ID, "인턴 경험");
            List<Experience> experiences = List.of(experience1, experience2);

            PortfolioStrategy draftStrategy = PortfolioStrategy.createDraft(
                    USER_ID,
                    POST_ID,
                    POST_ANALYSIS_ID,
                    Instant.now(),
                    LocalDate.now(ZoneId.of("Asia/Seoul"))
            );
            ReflectionTestUtils.setField(draftStrategy, "id", 100L);
            ReflectionTestUtils.setField(portfolioStrategyService, "weeklyLimitEnabled", true);

            when(experienceRepository.findAllByPublicIdInAndUserId(anyList(), anyLong()))
                    .thenReturn(experiences);
            when(aiUsagePolicyService.reserve(USER_ID, AiUsageType.PORTFOLIO_STRATEGY))
                    .thenReturn(true);
            when(portfolioStrategyRepository.findFirstByUserIdAndPostAnalysisIdAndStatusOrderByCreatedAtDesc(
                    USER_ID,
                    POST_ANALYSIS_ID,
                    PortfolioStrategyGenerateStatus.DRAFT
            )).thenReturn(Optional.of(draftStrategy));

            // when
            GeneratePortfolioStrategyResult result = portfolioStrategyService.generate(USER_ID, req);

            // then
            assertNotNull(result);
            assertEquals(draftStrategy.getPublicId(), result.strategyId());
            assertEquals(PortfolioStrategyGenerateStatus.PROCESSING, draftStrategy.getStatus());
            assertNull(draftStrategy.getJobType());
            assertNull(draftStrategy.getIndustryId());
            assertEquals(experiences.size(), draftStrategy.getSelectedExperienceCount());

            verify(portfolioStrategyRepository).save(draftStrategy);
            verify(portfolioStrategyContentGenerator).request(
                    eq(USER_ID),
                    eq(100L),
                    eq(experiences),
                    eq(POST_ANALYSIS_ID)
            );
            verifyNoInteractions(industryRepository);
        }

        @Test
        @DisplayName("직무와 산업 없이 draft를 PROCESSING으로 전환하고 AI 생성 요청을 보낸다")
        void generate_success_withoutJobTypeAndIndustry() {
            // given
            GeneratePortfolioStrategyRequest req = new GeneratePortfolioStrategyRequest(
                    POST_ANALYSIS_PUBLIC_ID,
                    List.of(EXPERIENCE_PUBLIC_ID_1)
            );

            Experience experience = createExperience(USER_ID, "캡스톤 프로젝트");
            List<Experience> experiences = List.of(experience);

            PortfolioStrategy draftStrategy = PortfolioStrategy.createDraft(
                    USER_ID,
                    POST_ID,
                    POST_ANALYSIS_ID,
                    Instant.now(),
                    LocalDate.now(ZoneId.of("Asia/Seoul"))
            );
            ReflectionTestUtils.setField(draftStrategy, "id", 100L);

            when(experienceRepository.findAllByPublicIdInAndUserId(req.experienceIds(), USER_ID))
                    .thenReturn(experiences);
            when(portfolioStrategyRepository.findFirstByUserIdAndPostAnalysisIdAndStatusOrderByCreatedAtDesc(
                    USER_ID,
                    POST_ANALYSIS_ID,
                    PortfolioStrategyGenerateStatus.DRAFT
            )).thenReturn(Optional.of(draftStrategy));
            when(aiUsagePolicyService.reserve(USER_ID, AiUsageType.PORTFOLIO_STRATEGY))
                    .thenReturn(true);

            // when
            GeneratePortfolioStrategyResult result = portfolioStrategyService.generate(USER_ID, req);

            // then
            assertNotNull(result);
            assertEquals(draftStrategy.getPublicId(), result.strategyId());
            assertEquals(PortfolioStrategyGenerateStatus.PROCESSING, draftStrategy.getStatus());
            assertNull(draftStrategy.getJobType());
            assertNull(draftStrategy.getIndustryId());

            verify(portfolioStrategyContentGenerator).request(
                    eq(USER_ID),
                    eq(100L),
                    eq(experiences),
                    eq(POST_ANALYSIS_ID)
            );
            verifyNoInteractions(industryRepository);
        }

        @Test
        @DisplayName("이번 주 성공한 전략 생성 횟수가 7회이면 다시 생성할 수 없다")
        void generate_fail_whenWeeklyLimitReached() {
            // given
            GeneratePortfolioStrategyRequest req = new GeneratePortfolioStrategyRequest(
                    POST_ANALYSIS_PUBLIC_ID,
                    List.of(EXPERIENCE_PUBLIC_ID_1)
            );

            Experience experience = createExperience(USER_ID, "캡스톤 프로젝트");
            PortfolioStrategy draftStrategy = PortfolioStrategy.createDraft(
                    USER_ID,
                    POST_ID,
                    POST_ANALYSIS_ID,
                    Instant.now(),
                    LocalDate.now(ZoneId.of("Asia/Seoul"))
            );
            ReflectionTestUtils.setField(draftStrategy, "id", 100L);

            when(experienceRepository.findAllByPublicIdInAndUserId(req.experienceIds(), USER_ID))
                    .thenReturn(List.of(experience));
            when(portfolioStrategyRepository.findFirstByUserIdAndPostAnalysisIdAndStatusOrderByCreatedAtDesc(
                    USER_ID,
                    POST_ANALYSIS_ID,
                    PortfolioStrategyGenerateStatus.DRAFT
            )).thenReturn(Optional.of(draftStrategy));
            when(aiUsagePolicyService.reserve(USER_ID, AiUsageType.PORTFOLIO_STRATEGY))
                    .thenReturn(false);

            // when
            BaseException exception = assertThrows(
                    BaseException.class,
                    () -> portfolioStrategyService.generate(USER_ID, req)
            );

            // then
            assertEquals(PortfolioStrategyErrorCode.WEEKLY_LIMIT_EXCEEDED, exception.getErrorCode());
            verify(aiUsagePolicyService).reserve(USER_ID, AiUsageType.PORTFOLIO_STRATEGY);
            verify(experienceRepository).findAllByPublicIdInAndUserId(req.experienceIds(), USER_ID);
            verify(portfolioStrategyRepository, never()).save(any(PortfolioStrategy.class));
        }

        @Test
        @DisplayName("이번 주 성공한 전략 생성 횟수가 7회이면 WEEKLY_LIMIT_EXCEEDED 예외가 발생한다")
        void generate_fail_whenWeeklyLimitReachedForMultipleExperiences() throws Exception {
            // given
            setField(portfolioStrategyService, "weeklyLimitEnabled", true);

            GeneratePortfolioStrategyRequest req =
                    new GeneratePortfolioStrategyRequest(POST_ANALYSIS_PUBLIC_ID, List.of(EXPERIENCE_PUBLIC_ID_1, EXPERIENCE_PUBLIC_ID_2));

            Experience experience1 = createExperience(USER_ID, "캡스톤 프로젝트");
            Experience experience2 = createExperience(USER_ID, "인턴 경험");
            PortfolioStrategy draftStrategy = PortfolioStrategy.createDraft(
                    USER_ID,
                    POST_ID,
                    POST_ANALYSIS_ID,
                    Instant.now(),
                    LocalDate.now(ZoneId.of("Asia/Seoul"))
            );
            ReflectionTestUtils.setField(draftStrategy, "id", 100L);

            when(experienceRepository.findAllByPublicIdInAndUserId(req.experienceIds(), USER_ID))
                    .thenReturn(List.of(experience1, experience2));
            when(portfolioStrategyRepository.findFirstByUserIdAndPostAnalysisIdAndStatusOrderByCreatedAtDesc(
                    USER_ID,
                    POST_ANALYSIS_ID,
                    PortfolioStrategyGenerateStatus.DRAFT
            )).thenReturn(Optional.of(draftStrategy));
            when(aiUsagePolicyService.reserve(USER_ID, AiUsageType.PORTFOLIO_STRATEGY))
                    .thenReturn(false);

            // when & then
            BaseException ex = assertThrows(BaseException.class,
                    () -> portfolioStrategyService.generate(USER_ID, req));

            assertEquals(PortfolioStrategyErrorCode.WEEKLY_LIMIT_EXCEEDED, ex.getErrorCode());

            verify(aiUsagePolicyService).reserve(USER_ID, AiUsageType.PORTFOLIO_STRATEGY);
        }

        @Test
        @DisplayName("이번 주 성공한 전략 생성 횟수가 7회 미만이면 draft를 실행하고 AI 생성 요청을 보낸다")
        void generate_success_whenWeeklyLimitAvailable() throws Exception{
            // given
            setField(portfolioStrategyService, "weeklyLimitEnabled", true);

            GeneratePortfolioStrategyRequest req =
                    new GeneratePortfolioStrategyRequest(POST_ANALYSIS_PUBLIC_ID, List.of(EXPERIENCE_PUBLIC_ID_1, EXPERIENCE_PUBLIC_ID_2));

            Experience experience1 = createExperience(USER_ID, "캡스톤 프로젝트");
            Experience experience2 = createExperience(USER_ID, "인턴 경험");
            List<Experience> experiences = List.of(experience1, experience2);

            PortfolioStrategy draftStrategy = PortfolioStrategy.createDraft(
                    USER_ID,
                    POST_ID,
                    POST_ANALYSIS_ID,
                    Instant.now(),
                    LocalDate.now(ZoneId.of("Asia/Seoul"))
            );
            setField(draftStrategy, "id", 100L);

            when(experienceRepository.findAllByPublicIdInAndUserId(req.experienceIds(), USER_ID))
                    .thenReturn(experiences);
            when(aiUsagePolicyService.reserve(USER_ID, AiUsageType.PORTFOLIO_STRATEGY))
                    .thenReturn(true);
            when(portfolioStrategyRepository.findFirstByUserIdAndPostAnalysisIdAndStatusOrderByCreatedAtDesc(
                    USER_ID,
                    POST_ANALYSIS_ID,
                    PortfolioStrategyGenerateStatus.DRAFT
            )).thenReturn(Optional.of(draftStrategy));

            // when
            GeneratePortfolioStrategyResult result = portfolioStrategyService.generate(USER_ID, req);

            // then
            assertNotNull(result);
            assertEquals(draftStrategy.getPublicId(), result.strategyId());

            verify(aiUsagePolicyService).reserve(USER_ID, AiUsageType.PORTFOLIO_STRATEGY);
            verify(experienceRepository)
                    .findAllByPublicIdInAndUserId(req.experienceIds(), USER_ID);
            verify(portfolioStrategyRepository)
                    .save(draftStrategy);
            verify(portfolioStrategyContentGenerator)
                    .request(
                            eq(USER_ID),
                            eq(100L),
                            eq(experiences),
                            eq(POST_ANALYSIS_ID)
                    );
            verifyNoInteractions(industryRepository);
        }
    }

    @Nested
    @DisplayName("getList")
    class GetListTest {

        @Test
        @DisplayName("첫 조회는 size보다 하나 더 조회해 nextCursor와 hasNext를 반환한다")
        void getPortfolioStrategyList_success() throws Exception {
            // given
            UUID strategyPublicId1 = UUID.randomUUID();
            UUID postPublicId1 = UUID.randomUUID();
            UUID postAnalysisPublicId1 = UUID.randomUUID();
            UUID strategyPublicId2 = UUID.randomUUID();
            UUID postPublicId2 = UUID.randomUUID();
            UUID postAnalysisPublicId2 = UUID.randomUUID();
            PortfolioStrategyListQueryItem strategy1 = createPortfolioStrategyListQueryItem(
                    100L,
                    strategyPublicId1,
                    postPublicId1,
                    postAnalysisPublicId1,
                    "백엔드 개발자 채용",
                    JobType.BACKEND,
                    "핀테크",
                    PortfolioStrategyGenerateStatus.READY,
                    Instant.parse("2026-03-10T10:00:00Z")
            );

            PortfolioStrategyListQueryItem strategy2 = createPortfolioStrategyListQueryItem(
                    99L,
                    strategyPublicId2,
                    postPublicId2,
                    postAnalysisPublicId2,
                    "서버 개발자 채용",
                    JobType.BACKEND,
                    "마스터",
                    PortfolioStrategyGenerateStatus.DRAFT,
                    Instant.parse("2026-03-09T10:00:00Z")
            );

            PortfolioStrategyListQueryItem strategy3 = createPortfolioStrategyListQueryItem(
                    98L,
                    UUID.randomUUID(),
                    UUID.randomUUID(),
                    UUID.randomUUID(),
                    "세 번째 채용",
                    JobType.BACKEND,
                    "마스터",
                    PortfolioStrategyGenerateStatus.READY,
                    Instant.parse("2026-03-08T10:00:00Z")
            );

            when(portfolioStrategyRepository.findFirstPortfolioStrategyListByUserId(
                    eq(USER_ID),
                    any(Pageable.class)
            )).thenReturn(List.of(strategy1, strategy2, strategy3));

            // when
            PortfolioStrategyListResult result =
                    portfolioStrategyService.getPortfolioStrategyList(USER_ID, null, 2);

            // then
            assertNotNull(result);
            assertTrue(result.hasNext());
            assertNotNull(result.nextCursor());
            assertEquals(2, result.contents().size());

            PortfolioStrategyListResultItem first = result.contents().get(0);
            assertEquals(strategyPublicId1, first.strategyId());
            assertEquals(postPublicId1, first.postId());
            assertEquals(postAnalysisPublicId1, first.postAnalysisId());
            assertEquals("백엔드 개발자 채용", first.postAnalysisTitle());
            assertEquals(JobType.BACKEND, first.jobType());
            assertEquals("핀테크", first.industryName());
            assertEquals(PortfolioStrategyGenerateStatus.READY, first.status());
            assertEquals(Instant.parse("2026-03-10T10:00:00Z"), first.createdAt());

            PortfolioStrategyListResultItem second = result.contents().get(1);
            assertEquals(strategyPublicId2, second.strategyId());
            assertEquals(postPublicId2, second.postId());
            assertEquals(postAnalysisPublicId2, second.postAnalysisId());
            assertEquals("서버 개발자 채용", second.postAnalysisTitle());
            assertEquals(JobType.BACKEND, second.jobType());
            assertEquals("마스터", second.industryName());
            assertEquals(PortfolioStrategyGenerateStatus.DRAFT, second.status());
            assertEquals(Instant.parse("2026-03-09T10:00:00Z"), second.createdAt());

            PortfolioStrategyCursor nextCursor = PortfolioStrategyCursor.decode(result.nextCursor());
            assertEquals(strategy2.createdAt(), nextCursor.createdAt());
            assertEquals(strategy2.id(), nextCursor.id());

            ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
            verify(portfolioStrategyRepository).findFirstPortfolioStrategyListByUserId(
                    eq(USER_ID),
                    pageableCaptor.capture()
            );
            assertEquals(3, pageableCaptor.getValue().getPageSize());
        }

        @Test
        @DisplayName("커서가 있으면 커서보다 뒤의 전략을 조회한다")
        void getPortfolioStrategyList_withCursor() {
            // given
            PortfolioStrategyCursor cursor = new PortfolioStrategyCursor(
                    Instant.parse("2026-03-09T10:00:00Z"),
                    99L
            );
            when(portfolioStrategyRepository.findNextPortfolioStrategyListByUserId(
                    eq(USER_ID),
                    eq(cursor.createdAt()),
                    eq(cursor.id()),
                    any(Pageable.class)
            ))
                    .thenReturn(List.of());

            // when
            PortfolioStrategyListResult result =
                    portfolioStrategyService.getPortfolioStrategyList(USER_ID, cursor.encode(), 20);

            // then
            assertNotNull(result);
            assertFalse(result.hasNext());
            assertNull(result.nextCursor());
            assertNotNull(result.contents());
            assertTrue(result.contents().isEmpty());

            verify(portfolioStrategyRepository).findNextPortfolioStrategyListByUserId(
                    eq(USER_ID),
                    eq(cursor.createdAt()),
                    eq(cursor.id()),
                    any(Pageable.class)
            );
            verify(portfolioStrategyRepository, never())
                    .findFirstPortfolioStrategyListByUserId(anyLong(), any(Pageable.class));
        }

        @Test
        @DisplayName("유효하지 않은 커서는 INVALID_INPUT_VALUE 예외가 발생한다")
        void getPortfolioStrategyList_invalidCursor() {
            BaseException exception = assertThrows(
                    BaseException.class,
                    () -> portfolioStrategyService.getPortfolioStrategyList(USER_ID, "invalid", 20)
            );

            assertEquals(GlobalErrorCode.INVALID_INPUT_VALUE, exception.getErrorCode());
            verifyNoInteractions(portfolioStrategyRepository);
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
            UUID strategyPublicId = portfolioStrategy.getPublicId();
            UUID postPublicId = UUID.randomUUID();
            UUID postAnalysisPublicId = UUID.randomUUID();

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

            portfolioStrategy.addResult(String.valueOf(content));
            Industry industry = mock(Industry.class);
            when(industry.getName()).thenReturn("핀테크");

            when(portfolioStrategyRepository.findPortfolioStrategyDetailByPublicIdAndUserId(strategyPublicId, USER_ID))
                    .thenReturn(Optional.of(new PortfolioStrategyDetailQueryResult(
                            portfolioStrategy,
                            postPublicId,
                            postAnalysisPublicId,
                            "백엔드 개발자 채용"
                    )));
            when(objectMapper.readValue(portfolioStrategy.getResultJson(), PortfolioStrategyContent.class))
                    .thenReturn(content);
            when(industryRepository.findById(INDUSTRY_ID))
                    .thenReturn(Optional.of(industry));

            // when
            PortfolioStrategyDetailResult result =
                    portfolioStrategyService.getPortfolioStrategyDetail(strategyPublicId, USER_ID);

            // then
            assertNotNull(result);
            assertEquals(strategyPublicId, result.strategyId());
            assertEquals(postPublicId, result.postId());
            assertEquals(postAnalysisPublicId, result.postAnalysisId());
            assertEquals("백엔드 개발자 채용", result.postAnalysisTitle());
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

            verify(portfolioStrategyRepository).findPortfolioStrategyDetailByPublicIdAndUserId(strategyPublicId, USER_ID);
            verify(objectMapper).readValue(portfolioStrategy.getResultJson(), PortfolioStrategyContent.class);
        }

        @Test
        @DisplayName("해당 사용자의 전략이 없으면 NOT_FOUND 예외가 발생한다")
        void getPortfolioStrategyDetail_fail_whenStrategyNotFound() {
            // given
            UUID strategyId = UUID.randomUUID();

            when(portfolioStrategyRepository.findPortfolioStrategyDetailByPublicIdAndUserId(strategyId, USER_ID))
                    .thenReturn(Optional.empty());

            // when
            BaseException exception = assertThrows(
                    BaseException.class,
                    () -> portfolioStrategyService.getPortfolioStrategyDetail(strategyId, USER_ID)
            );

            // then
            assertEquals(PortfolioStrategyErrorCode.NOT_FOUND, exception.getErrorCode());
            verify(portfolioStrategyRepository).findPortfolioStrategyDetailByPublicIdAndUserId(strategyId, USER_ID);
            verifyNoInteractions(objectMapper);
        }

        @Test
        @DisplayName("DRAFT 상태의 전략을 상세 조회하면 RESULT_NOT_READY 예외가 발생한다")
        void getPortfolioStrategyDetail_fail_whenDraft() {
            // given
            Long strategyId = 100L;
            PortfolioStrategy portfolioStrategy = PortfolioStrategy.createDraft(
                    USER_ID,
                    POST_ID,
                    POST_ANALYSIS_ID,
                    Instant.parse("2026-03-10T10:00:00Z"),
                    LocalDate.of(2026, 3, 10)
            );
            ReflectionTestUtils.setField(portfolioStrategy, "id", strategyId);
            UUID strategyPublicId = portfolioStrategy.getPublicId();

            when(portfolioStrategyRepository.findPortfolioStrategyDetailByPublicIdAndUserId(strategyPublicId, USER_ID))
                    .thenReturn(Optional.of(new PortfolioStrategyDetailQueryResult(
                            portfolioStrategy,
                            UUID.randomUUID(),
                            UUID.randomUUID(),
                            "백엔드 개발자 채용"
                    )));

            // when
            BaseException exception = assertThrows(
                    BaseException.class,
                    () -> portfolioStrategyService.getPortfolioStrategyDetail(strategyPublicId, USER_ID)
            );

            // then
            assertEquals(PortfolioStrategyErrorCode.RESULT_NOT_READY, exception.getErrorCode());
            verify(portfolioStrategyRepository).findPortfolioStrategyDetailByPublicIdAndUserId(strategyPublicId, USER_ID);
            verifyNoInteractions(objectMapper);
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
            UUID strategyPublicId = portfolioStrategy.getPublicId();

            when(portfolioStrategyRepository.findByPublicIdAndUserId(strategyPublicId, USER_ID))
                    .thenReturn(Optional.of(portfolioStrategy));

            // when
            portfolioStrategyService.deletePortfolioStrategy(strategyPublicId, USER_ID);

            // then
            verify(portfolioStrategyRepository).findByPublicIdAndUserId(strategyPublicId, USER_ID);
            verify(portfolioStrategyRepository).delete(portfolioStrategy);
        }

        @Test
        @DisplayName("해당 사용자의 포트폴리오 전략이 없으면 NOT_FOUND 예외가 발생한다")
        void deletePortfolioStrategy_fail_whenStrategyNotFound() {
            // given
            UUID strategyId = UUID.randomUUID();

            when(portfolioStrategyRepository.findByPublicIdAndUserId(strategyId, USER_ID))
                    .thenReturn(Optional.empty());

            // when
            BaseException exception = assertThrows(
                    BaseException.class,
                    () -> portfolioStrategyService.deletePortfolioStrategy(strategyId, USER_ID)
            );

            // then
            assertEquals(PortfolioStrategyErrorCode.NOT_FOUND, exception.getErrorCode());
            verify(portfolioStrategyRepository).findByPublicIdAndUserId(strategyId, USER_ID);
            verify(portfolioStrategyRepository, never()).delete(any(PortfolioStrategy.class));
        }
    }

    private PortfolioStrategyListQueryItem createPortfolioStrategyListQueryItem(
            Long id,
            UUID strategyId,
            UUID postId,
            UUID postAnalysisId,
            String postAnalysisTitle,
            JobType jobType,
            String industryName,
            PortfolioStrategyGenerateStatus status,
            Instant createdAt
    ) {
        return new PortfolioStrategyListQueryItem(
                id,
                strategyId,
                postId,
                postAnalysisId,
                postAnalysisTitle,
                jobType,
                industryName,
                status,
                createdAt
        );
    }

    private PortfolioStrategy createPortfolioStrategy(
            Long id,
            Long userId,
            JobType jobType,
            Long industryId,
            Instant createdAt
    ) throws Exception {
        LocalDate generatedDate = createdAt.atZone(ZoneId.of("Asia/Seoul")).toLocalDate();

        PortfolioStrategy strategy = PortfolioStrategy.create(
                userId,
                POST_ID,
                jobType,
                industryId,
                POST_ANALYSIS_ID,
                1,
                createdAt,
                generatedDate
        );

        setField(strategy, "id", id);

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

    private Industry createIndustry(Long id, String name) {
        Industry industry = mock(Industry.class);
        ReflectionTestUtils.setField(industry, "id", id);
        ReflectionTestUtils.setField(industry, "name", name);
        return industry;
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
