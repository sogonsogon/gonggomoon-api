package com.sogonsogon.gonggomoon.domain.strategy.application;

import com.sogonsogon.gonggomoon.domain.experience.domain.FileAsset;
import com.sogonsogon.gonggomoon.domain.experience.domain.FileAssetRepository;
import com.sogonsogon.gonggomoon.domain.strategy.api.request.GenerateInterviewQuestionSetRequest;
import com.sogonsogon.gonggomoon.domain.strategy.application.result.GenerateInterviewQuestionSetResult;
import com.sogonsogon.gonggomoon.domain.strategy.application.result.InterviewQuestionSetListResult;
import com.sogonsogon.gonggomoon.domain.strategy.application.result.InterviewStrategiesResultItem;
import com.sogonsogon.gonggomoon.domain.strategy.application.result.InterviewStrategyDetailResult;
import com.sogonsogon.gonggomoon.domain.strategy.application.result.InterviewStrategyDetailResultItem;
import com.sogonsogon.gonggomoon.domain.strategy.domain.InterviewQuestion;
import com.sogonsogon.gonggomoon.domain.strategy.domain.InterviewStrategy;
import com.sogonsogon.gonggomoon.domain.strategy.domain.InterviewStrategyRepository;
import com.sogonsogon.gonggomoon.domain.strategy.domain.QuestionLevel;
import com.sogonsogon.gonggomoon.domain.strategy.error.InterviewStrategyErrorCode;
import com.sogonsogon.gonggomoon.domain.strategy.generator.InterviewStrategyQuestionSetGenerator;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class InterviewStrategyServiceTest {
    @Mock
    private FileAssetRepository fileAssetRepository;

    @Mock
    private InterviewStrategyRepository interviewStrategyRepository;

    @Mock
    private InterviewStrategyQuestionSetGenerator interviewStrategyQuestionSetGenerator;

    @InjectMocks
    private InterviewStrategyService interviewStrategyService;

    private static final Long USER_ID = 1L;
    private static final Long FILE_ASSET_ID = 10L;
    private static final Long INTERVIEW_STRATEGY_ID = 100L;
    private static final Long PORTFOLIO_FILE_ASSET_ID = 200L;

    @Nested
    @DisplayName("generate")
    class Generate {

        @Test
        @DisplayName("fileAssetId가 null이면 PORTFOLIO_FILE_ASSET_ID_REQUIRED 예외를 던진다")
        void generate_fail_when_fileAssetId_is_null() {
            // given
            GenerateInterviewQuestionSetRequest req = new GenerateInterviewQuestionSetRequest(null);

            // when
            BaseException exception = assertThrows(
                    BaseException.class,
                    () -> interviewStrategyService.generate(USER_ID, req)
            );

            // then
            assertEquals(InterviewStrategyErrorCode.FILE_ASSET_ID_REQUIRED, exception.getErrorCode());
            then(fileAssetRepository).shouldHaveNoInteractions();
            then(interviewStrategyQuestionSetGenerator).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("사용자의 파일을 찾을 수 없으면 FILE_ASSET_NOT_FOUND 예외를 던진다")
        void generate_fail_when_fileAsset_not_found() {
            // given
            GenerateInterviewQuestionSetRequest req = new GenerateInterviewQuestionSetRequest(FILE_ASSET_ID);

            given(fileAssetRepository.findByIdAndUserId(FILE_ASSET_ID, USER_ID))
                    .willReturn(Optional.empty());

            // when
            BaseException exception = assertThrows(
                    BaseException.class,
                    () -> interviewStrategyService.generate(USER_ID, req)
            );

            // then
            assertEquals(InterviewStrategyErrorCode.FILE_ASSET_NOT_FOUND, exception.getErrorCode());
            then(fileAssetRepository).should().findByIdAndUserId(FILE_ASSET_ID, USER_ID);
            then(interviewStrategyQuestionSetGenerator).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("정상 요청이면 질문 초안을 저장하고 AI 생성 요청을 보낸다")
        void generate_success_saveDraftAndRequestAi() {
            // given
            GenerateInterviewQuestionSetRequest req = new GenerateInterviewQuestionSetRequest(FILE_ASSET_ID);

            FileAsset fileAsset = mock(FileAsset.class);

            when(fileAssetRepository.findByIdAndUserId(FILE_ASSET_ID, USER_ID))
                    .thenReturn(Optional.of(fileAsset));

            when(interviewStrategyRepository.save(any(InterviewStrategy.class)))
                    .thenAnswer(invocation -> {
                        InterviewStrategy strategy = invocation.getArgument(0);
                        ReflectionTestUtils.setField(strategy, "id", INTERVIEW_STRATEGY_ID);
                        return strategy;
                    });

            // when
            GenerateInterviewQuestionSetResult result =
                    interviewStrategyService.generate(USER_ID, req);

            // then
            ArgumentCaptor<InterviewStrategy> strategyCaptor =
                    ArgumentCaptor.forClass(InterviewStrategy.class);

            verify(interviewStrategyRepository, times(1)).save(strategyCaptor.capture());
            InterviewStrategy savedStrategy = strategyCaptor.getValue();

            assertNotNull(savedStrategy);
            assertEquals(USER_ID, savedStrategy.getUserId());
            assertEquals(FILE_ASSET_ID, savedStrategy.getFileAssetId());
            assertNotNull(savedStrategy.getGeneratedDate());
            assertNotNull(savedStrategy.getCreatedAt());

            verify(fileAssetRepository, times(1)).findByIdAndUserId(FILE_ASSET_ID, USER_ID);
            verify(interviewStrategyQuestionSetGenerator, times(1))
                    .request(USER_ID, INTERVIEW_STRATEGY_ID);

            assertNotNull(result);
            assertEquals(INTERVIEW_STRATEGY_ID, result.interviewStrategyId());
        }

        @Test
        @DisplayName("오늘 이미 생성한 질문이 있으면 다시 생성할 수 없다")
        void generate_fail_whenStrategyAlreadyCreatedToday() {
            // given
            ReflectionTestUtils.setField(interviewStrategyService, "dailyLimitEnabled", true);
            GenerateInterviewQuestionSetRequest req = new GenerateInterviewQuestionSetRequest(FILE_ASSET_ID);

            when(interviewStrategyRepository.existsByUserIdAndGeneratedDate(eq(USER_ID), any(LocalDate.class)))
                    .thenReturn(true);

            // when
            BaseException exception = assertThrows(
                    BaseException.class,
                    () -> interviewStrategyService.generate(USER_ID, req)
            );

            // then
            assertEquals(InterviewStrategyErrorCode.ALREADY_CREATED_TODAY, exception.getErrorCode());
            verify(interviewStrategyRepository).existsByUserIdAndGeneratedDate(eq(USER_ID), any(LocalDate.class));
        }
    }

    @Nested
    @DisplayName("getInterviewStrategiesListTest")
    class GetInterviewStrategiesListTest {

        @Test
        @DisplayName("사용자의 면접 전략 질문 세트 목록을 생성일 내림차순으로 조회한다")
        void getInterviewStrategiesList_success() {
            // given
            Instant createdAt1 = Instant.parse("2026-03-12T01:00:00Z");
            Instant createdAt2 = Instant.parse("2026-03-11T01:00:00Z");

            InterviewStrategy strategy1 = mock(InterviewStrategy.class);
            InterviewStrategy strategy2 = mock(InterviewStrategy.class);

            when(strategy1.getId()).thenReturn(10L);
            when(strategy1.getCreatedAt()).thenReturn(createdAt1);

            when(strategy2.getId()).thenReturn(20L);
            when(strategy2.getCreatedAt()).thenReturn(createdAt2);

            when(interviewStrategyRepository.findAllByUserIdOrderByCreatedAtDesc(USER_ID))
                    .thenReturn(List.of(strategy1, strategy2));

            // when
            InterviewQuestionSetListResult result =
                    interviewStrategyService.getInterviewStrategiesList(USER_ID);

            // then
            assertNotNull(result);
            assertEquals(2, result.totalCount());
            assertEquals(2, result.contents().size());

            InterviewStrategiesResultItem first = result.contents().get(0);
            assertEquals(10L, first.interviewStrategyId());
            assertEquals(createdAt1, first.createdAt());

            InterviewStrategiesResultItem second = result.contents().get(1);
            assertEquals(20L, second.interviewStrategyId());
            assertEquals(createdAt2, second.createdAt());

            verify(interviewStrategyRepository, times(1))
                    .findAllByUserIdOrderByCreatedAtDesc(USER_ID);
        }

        @Test
        @DisplayName("조회된 면접 전략 질문 세트가 없으면 빈 목록을 반환한다")
        void getInterviewStrategiesList_empty() {
            // given
            when(interviewStrategyRepository.findAllByUserIdOrderByCreatedAtDesc(USER_ID))
                    .thenReturn(List.of());

            // when
            InterviewQuestionSetListResult result =
                    interviewStrategyService.getInterviewStrategiesList(USER_ID);

            // then
            assertNotNull(result);
            assertEquals(0, result.totalCount());
            assertTrue(result.contents().isEmpty());

            verify(interviewStrategyRepository, times(1))
                    .findAllByUserIdOrderByCreatedAtDesc(USER_ID);
        }
    }

    @Nested
    @DisplayName("GetInterviewStrategyDetailTest")
    class GetInterviewStrategyDetailTest {

        @Test
        @DisplayName("면접 전략 질문 세트 상세를 정상 조회한다")
        void getInterviewStrategyDetail_success() {
            // given
            Instant createdAt = Instant.parse("2026-03-12T01:00:00Z");

            InterviewQuestion question1 = mock(InterviewQuestion.class);
            InterviewQuestion question2 = mock(InterviewQuestion.class);

            when(question1.getId()).thenReturn(1L);
            when(question1.getQuestion()).thenReturn("프로젝트에서 가장 어려웠던 문제는 무엇인가요?");
            when(question1.getQuestionLevel()).thenReturn(QuestionLevel.LOWER);

            when(question2.getId()).thenReturn(2L);
            when(question2.getQuestion()).thenReturn("트래픽 증가 상황에서 어떻게 대응하시겠습니까?");
            when(question2.getQuestionLevel()).thenReturn(QuestionLevel.HIGH);

            InterviewStrategy interviewStrategy = mock(InterviewStrategy.class);
            when(interviewStrategy.getId()).thenReturn(INTERVIEW_STRATEGY_ID);
            when(interviewStrategy.getFileAssetId()).thenReturn(PORTFOLIO_FILE_ASSET_ID);
            when(interviewStrategy.getCreatedAt()).thenReturn(createdAt);
            when(interviewStrategy.getQuestions()).thenReturn(List.of(question1, question2));

            FileAsset fileAsset = mock(FileAsset.class);
            when(fileAsset.getOriginalFileName()).thenReturn("backend_portfolio.pdf");

            when(interviewStrategyRepository.findByIdAndUserId(INTERVIEW_STRATEGY_ID, USER_ID))
                    .thenReturn(Optional.of(interviewStrategy));
            when(fileAssetRepository.findById(PORTFOLIO_FILE_ASSET_ID))
                    .thenReturn(Optional.of(fileAsset));

            // when
            InterviewStrategyDetailResult result =
                    interviewStrategyService.getInterviewStrategyDetail(INTERVIEW_STRATEGY_ID, USER_ID);

            // then
            assertNotNull(result);
            assertEquals(INTERVIEW_STRATEGY_ID, result.interviewStrategyId());
            assertEquals("backend_portfolio.pdf", result.basePortfolio());
            assertEquals(createdAt, result.createdAt());
            assertEquals(2, result.questionTotalCount());
            assertEquals(2, result.contents().size());

            InterviewStrategyDetailResultItem first = result.contents().get(0);
            assertEquals(1L, first.questionId());
            assertEquals("프로젝트에서 가장 어려웠던 문제는 무엇인가요?", first.question());
            assertEquals(QuestionLevel.LOWER, first.questionLevel());

            InterviewStrategyDetailResultItem second = result.contents().get(1);
            assertEquals(2L, second.questionId());
            assertEquals("트래픽 증가 상황에서 어떻게 대응하시겠습니까?", second.question());
            assertEquals(QuestionLevel.HIGH, second.questionLevel());

            verify(interviewStrategyRepository, times(1))
                    .findByIdAndUserId(INTERVIEW_STRATEGY_ID, USER_ID);
            verify(fileAssetRepository, times(1))
                    .findById(PORTFOLIO_FILE_ASSET_ID);
        }

        @Test
        @DisplayName("존재하지 않는 면접 전략 질문 세트면 예외가 발생한다")
        void getInterviewStrategyDetail_notFound() {
            // given
            when(interviewStrategyRepository.findByIdAndUserId(INTERVIEW_STRATEGY_ID, USER_ID))
                    .thenReturn(Optional.empty());

            // when
            BaseException exception = assertThrows(
                    BaseException.class,
                    () -> interviewStrategyService.getInterviewStrategyDetail(INTERVIEW_STRATEGY_ID, USER_ID)
            );

            // then
            assertEquals(InterviewStrategyErrorCode.NOT_FOUND, exception.getErrorCode());

            verify(interviewStrategyRepository, times(1))
                    .findByIdAndUserId(INTERVIEW_STRATEGY_ID, USER_ID);
            verify(fileAssetRepository, never()).findById(anyLong());
        }

        @Test
        @DisplayName("연결된 파일(포트폴리오)이 없으면 예외가 발생한다")
        void getInterviewStrategyDetail_fileAssetNotFound() {
            // given
            InterviewStrategy interviewStrategy = mock(InterviewStrategy.class);

            when(interviewStrategy.getFileAssetId()).thenReturn(PORTFOLIO_FILE_ASSET_ID);

            when(interviewStrategyRepository.findByIdAndUserId(INTERVIEW_STRATEGY_ID, USER_ID))
                    .thenReturn(Optional.of(interviewStrategy));
            when(fileAssetRepository.findById(PORTFOLIO_FILE_ASSET_ID))
                    .thenReturn(Optional.empty());

            // when
            BaseException exception = assertThrows(
                    BaseException.class,
                    () -> interviewStrategyService.getInterviewStrategyDetail(INTERVIEW_STRATEGY_ID, USER_ID)
            );

            // then
            assertEquals(InterviewStrategyErrorCode.FILE_ASSET_NOT_FOUND, exception.getErrorCode());

            verify(interviewStrategyRepository, times(1))
                    .findByIdAndUserId(INTERVIEW_STRATEGY_ID, USER_ID);
            verify(fileAssetRepository, times(1))
                    .findById(PORTFOLIO_FILE_ASSET_ID);
        }
    }

    @Nested
    @DisplayName("deleteInterviewStrategy")
    class DeleteInterviewStrategyTest {
        @Test
        @DisplayName("면접 전략 질문 세트를 정상 삭제한다")
        void deleteInterviewStrategy_success() {
            // given
            InterviewStrategy interviewStrategy = mock(InterviewStrategy.class);

            when(interviewStrategyRepository.findByIdAndUserId(INTERVIEW_STRATEGY_ID, USER_ID))
                    .thenReturn(Optional.of(interviewStrategy));

            // when
            interviewStrategyService.deleteInterviewStrategy(INTERVIEW_STRATEGY_ID, USER_ID);

            // then
            verify(interviewStrategyRepository, times(1))
                    .findByIdAndUserId(INTERVIEW_STRATEGY_ID, USER_ID);
            verify(interviewStrategyRepository, times(1))
                    .delete(interviewStrategy);
        }

        @Test
        @DisplayName("존재하지 않는 면접 전략 질문 세트면 예외가 발생한다")
        void deleteInterviewStrategy_notFound() {
            // given
            when(interviewStrategyRepository.findByIdAndUserId(INTERVIEW_STRATEGY_ID, USER_ID))
                    .thenReturn(Optional.empty());

            // when
            BaseException exception = assertThrows(
                    BaseException.class,
                    () -> interviewStrategyService.deleteInterviewStrategy(INTERVIEW_STRATEGY_ID, USER_ID)
            );

            // then
            assertEquals(InterviewStrategyErrorCode.NOT_FOUND, exception.getErrorCode());

            verify(interviewStrategyRepository, times(1))
                    .findByIdAndUserId(INTERVIEW_STRATEGY_ID, USER_ID);
            verify(interviewStrategyRepository, never())
                    .delete(any(InterviewStrategy.class));
        }
    }
}
