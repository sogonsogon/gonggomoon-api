package com.sogonsogon.gonggomoon.domain.strategy.application;

import com.sogonsogon.gonggomoon.domain.experience.domain.FileAsset;
import com.sogonsogon.gonggomoon.domain.experience.domain.FileAssetRepository;
import com.sogonsogon.gonggomoon.domain.strategy.api.request.GenerateInterviewQuestionSetRequest;
import com.sogonsogon.gonggomoon.domain.strategy.application.result.GenerateInterviewQuestionSetResult;
import com.sogonsogon.gonggomoon.domain.strategy.application.result.InterviewQuestionSetListResult;
import com.sogonsogon.gonggomoon.domain.strategy.application.result.InterviewStrategiesResultItem;
import com.sogonsogon.gonggomoon.domain.strategy.domain.InterviewQuestion;
import com.sogonsogon.gonggomoon.domain.strategy.domain.InterviewStrategy;
import com.sogonsogon.gonggomoon.domain.strategy.domain.InterviewStrategyRepository;
import com.sogonsogon.gonggomoon.domain.strategy.domain.QuestionLevel;
import com.sogonsogon.gonggomoon.domain.strategy.error.InterviewStrategyErrorCode;
import com.sogonsogon.gonggomoon.domain.strategy.generator.InterviewStrategyQuestionSetGenerator;
import com.sogonsogon.gonggomoon.domain.strategy.generator.result.InterviewQuestionItem;
import com.sogonsogon.gonggomoon.domain.strategy.generator.result.InterviewStrategyQuestionSet;
import com.sogonsogon.gonggomoon.global.error.BaseException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
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
            assertEquals(InterviewStrategyErrorCode.PORTFOLIO_FILE_ASSET_ID_REQUIRED, exception.getErrorCode());
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
        @DisplayName("정상 요청이면 면접 전략을 생성하고 interviewStrategyId를 반환한다")
        void generate_success() {
            // given
            GenerateInterviewQuestionSetRequest req = new GenerateInterviewQuestionSetRequest(FILE_ASSET_ID);
            FileAsset fileAsset = mock(FileAsset.class);

            InterviewStrategyQuestionSet questionSet = InterviewStrategyQuestionSet.of(
                    List.of(
                            new InterviewQuestionItem("질문1", QuestionLevel.HIGH),
                            new InterviewQuestionItem("질문2", QuestionLevel.MIDDLE),
                            new InterviewQuestionItem("질문3", QuestionLevel.HIGH)
                    )
            );

            InterviewStrategy savedStrategy = InterviewStrategy.create(USER_ID, FILE_ASSET_ID);
            ReflectionTestUtils.setField(savedStrategy, "id", 100L);
            savedStrategy.addQuestions(
                    questionSet.questions().stream()
                            .map(item -> InterviewQuestion.create(item.question(), item.questionLevel()))
                            .toList()
            );

            given(fileAssetRepository.findByIdAndUserId(FILE_ASSET_ID, USER_ID))
                    .willReturn(Optional.of(fileAsset));
            given(fileAsset.getId()).willReturn(FILE_ASSET_ID);
            given(interviewStrategyQuestionSetGenerator.generate(FILE_ASSET_ID))
                    .willReturn(questionSet);
            given(interviewStrategyRepository.save(any(InterviewStrategy.class)))
                    .willReturn(savedStrategy);

            // when
            GenerateInterviewQuestionSetResult result = interviewStrategyService.generate(USER_ID, req);

            // then
            assertNotNull(result);
            assertEquals(100L, result.interviewStrategyId());

            then(fileAssetRepository).should().findByIdAndUserId(FILE_ASSET_ID, USER_ID);
            then(interviewStrategyQuestionSetGenerator).should().generate(FILE_ASSET_ID);
            then(interviewStrategyRepository).should().save(any(InterviewStrategy.class));
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
}
