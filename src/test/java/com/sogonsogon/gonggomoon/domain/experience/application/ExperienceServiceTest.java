package com.sogonsogon.gonggomoon.domain.experience.application;

import com.sogonsogon.gonggomoon.domain.experience.api.request.CreateExperienceRequest;
import com.sogonsogon.gonggomoon.domain.experience.api.request.UpdateExperienceRequest;
import com.sogonsogon.gonggomoon.domain.experience.application.result.CreateExperienceResult;
import com.sogonsogon.gonggomoon.domain.experience.application.result.ExperienceDetailResult;
import com.sogonsogon.gonggomoon.domain.experience.application.result.ExperienceListResult;
import com.sogonsogon.gonggomoon.domain.experience.domain.Experience;
import com.sogonsogon.gonggomoon.domain.experience.domain.ExperienceRepository;
import com.sogonsogon.gonggomoon.domain.experience.domain.ExperienceType;
import com.sogonsogon.gonggomoon.domain.experience.error.ExperienceErrorCode;
import com.sogonsogon.gonggomoon.global.error.BaseException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Nested;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExperienceServiceTest {

    @Mock
    private ExperienceRepository experienceRepository;

    @InjectMocks
    private ExperienceService experienceService;

    private final Long userId = 1L;
    private final Long experienceId = 100L;

    @Nested
    class CreateTest {

        @Test
        void create_정상요청이면_경험을_저장하고_결과를_반환한다() {
            // given
            CreateExperienceRequest req = new CreateExperienceRequest(
                    "프로젝트 경험",
                    ExperienceType.PROJECT,
                    "대용량 업로드 기능 구현",
                    LocalDate.of(2025, 1, 1),
                    LocalDate.of(2025, 2, 1)
            );

            Experience savedExperience = Experience.create(
                    userId,
                    req.title(),
                    req.experienceType(),
                    req.experienceContent(),
                    req.startDate(),
                    req.endDate()
            );

            when(experienceRepository.save(any(Experience.class))).thenReturn(savedExperience);

            // when
            CreateExperienceResult result = experienceService.create(userId, req);

            // then
            assertThat(result).isNotNull();
            verify(experienceRepository).save(any(Experience.class));
        }

        @Test
        void create_제목이_공백이면_예외가_발생한다() {
            // given
            CreateExperienceRequest req = new CreateExperienceRequest(
                    " ",
                    ExperienceType.PROJECT,
                    "대용량 업로드 기능 구현",
                    LocalDate.of(2025, 1, 1),
                    LocalDate.of(2025, 2, 1)
            );

            // when & then
            assertThatThrownBy(() -> experienceService.create(userId, req))
                    .isInstanceOf(BaseException.class);

            verify(experienceRepository, never()).save(any());
        }

        @Test
        void create_경험유형이_null이면_예외발생() {
            // given
            CreateExperienceRequest req = new CreateExperienceRequest(
                    "제목",
                    null,
                    "대용량 업로드 기능 구현",
                    LocalDate.of(2025, 1, 1),
                    LocalDate.of(2025, 2, 1)
            );

            // when & then
            assertThatThrownBy(() -> experienceService.create(userId, req))
                    .isInstanceOf(BaseException.class)
                    .extracting("errorCode")
                    .isEqualTo(ExperienceErrorCode.TYPE_REQUIRED);
            verify(experienceRepository, never()).save(any());
        }

        @Test
        void create_날짜범위가_잘못되면_예외발생() {
            // given
            CreateExperienceRequest req = new CreateExperienceRequest(
                    "제목",
                    ExperienceType.PROJECT,
                    "대용량 업로드 기능 구현",
                    LocalDate.of(2025, 1, 1),
                    LocalDate.of(2024, 12, 20)
            );

            // when & then
            assertThatThrownBy(() -> experienceService.create(userId, req))
                    .isInstanceOf(BaseException.class)
                    .extracting("errorCode")
                    .isEqualTo(ExperienceErrorCode.INVALID_DATE_RANGE);
            verify(experienceRepository, never()).save(any());
        }
    }

    @Nested
    class UpdateTest {

        @Test
        void update_정상요청이면_경험을_수정하고_상세결과를_반환한다() {
            // given
            Experience experience = Experience.create(
                    userId,
                    "기존 제목",
                    ExperienceType.PROJECT,
                    "기존 내용",
                    LocalDate.of(2025, 1, 1),
                    LocalDate.of(2025, 1, 31)
            );

            UpdateExperienceRequest req = new UpdateExperienceRequest(
                    "수정된 제목",
                    ExperienceType.CAREER,
                    "수정된 내용",
                    LocalDate.of(2025, 1, 1),
                    LocalDate.of(2025, 2, 28)
            );

            when(experienceRepository.findByIdAndUserId(experienceId, userId))
                    .thenReturn(Optional.of(experience));

            // when
            ExperienceDetailResult result = experienceService.update(experienceId, userId, req);

            // then
            assertThat(result).isNotNull();
            verify(experienceRepository).findByIdAndUserId(experienceId, userId);

            // then - 엔티티 상태 및 result 검증
            assertThat(experience.getTitle()).isEqualTo("수정된 제목");
            assertThat(result.title()).isEqualTo("수정된 제목");
            assertThat(result.experienceContent()).isEqualTo("수정된 내용");
        }

        @Test
        void update_존재하지_않는_경험이면_NOT_FOUND_예외가_발생한다() {
            // given
            UpdateExperienceRequest req = new UpdateExperienceRequest(
                    "수정된 제목",
                    ExperienceType.CAREER,
                    "수정된 내용",
                    LocalDate.of(2025, 1, 1),
                    LocalDate.of(2025, 2, 28)
            );

            // 찾지 못한 상황을 가정한다.
            when(experienceRepository.findByIdAndUserId(experienceId, userId))
                    .thenReturn(Optional.empty());

            // when & then - update를 실행했을 때 예외가 발생하는지 테스트
            assertThatThrownBy(() -> experienceService.update(experienceId, userId, req))
                    .isInstanceOf(BaseException.class) // BaseException이 발생하고
                    .extracting("errorCode") // errorCode가
                    .isEqualTo(ExperienceErrorCode.NOT_FOUND); // ExperienceErrorCode.NOT_FOUND가 맞는지

            verify(experienceRepository).findByIdAndUserId(experienceId, userId);
        }

        @Test
        void update_수정값이_유효하지_않으면_예외가_발생한다() {
            // given
            Experience experience = Experience.create(
                    userId,
                    "기존 제목",
                    ExperienceType.PROJECT,
                    "기존 내용",
                    LocalDate.of(2025, 1, 1),
                    LocalDate.of(2025, 1, 31)
            );

            UpdateExperienceRequest req = new UpdateExperienceRequest(
                    " ",
                    ExperienceType.CAREER,
                    "수정된 내용",
                    LocalDate.of(2025, 1, 1),
                    LocalDate.of(2025, 2, 28)
            );

            when(experienceRepository.findByIdAndUserId(experienceId, userId))
                    .thenReturn(Optional.of(experience));

            // when & then
            assertThatThrownBy(() -> experienceService.update(experienceId, userId, req))
                    .isInstanceOf(BaseException.class)
                    .extracting("errorCode")
                    .isEqualTo(ExperienceErrorCode.TITLE_REQUIRED);
        }

        @Test
        void update_날짜범위가_잘못되면_예외발생() {
            // given
            Experience experience = Experience.create(
                    userId,
                    "기존 제목",
                    ExperienceType.PROJECT,
                    "기존 내용",
                    LocalDate.of(2025, 1, 1),
                    LocalDate.of(2025, 1, 31)
            );

            UpdateExperienceRequest req = new UpdateExperienceRequest(
                    "수정 제목",
                    ExperienceType.CAREER,
                    "수정된 내용",
                    LocalDate.of(2025, 1, 1),
                    LocalDate.of(2024, 2, 28)
            );

            when(experienceRepository.findByIdAndUserId(experienceId, userId))
                    .thenReturn(Optional.of(experience));

            // when & then
            assertThatThrownBy(() -> experienceService.update(experienceId, userId, req))
                    .isInstanceOf(BaseException.class)
                    .extracting("errorCode")
                    .isEqualTo(ExperienceErrorCode.INVALID_DATE_RANGE);
        }
    }

    @Nested
    class DeleteTest {

        @Test
        void deleteExperience_존재하는_경험이면_삭제한다() {
            // given
            Experience experience = Experience.create(
                    userId,
                    "제목",
                    ExperienceType.PROJECT,
                    "내용",
                    LocalDate.of(2025, 1, 1),
                    LocalDate.of(2025, 1, 31)
            );

            when(experienceRepository.findByIdAndUserId(experienceId, userId))
                    .thenReturn(Optional.of(experience));

            // when
            experienceService.deleteExperience(experienceId, userId);

            // then
            verify(experienceRepository).findByIdAndUserId(experienceId, userId);
            verify(experienceRepository).delete(experience);
        }

        @Test
        void deleteExperience_존재하지_않는_경험이면_NOT_FOUND_예외가_발생한다() {
            // given
            when(experienceRepository.findByIdAndUserId(experienceId, userId))
                    .thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> experienceService.deleteExperience(experienceId, userId))
                    .isInstanceOf(BaseException.class)
                    .extracting("errorCode")
                    .isEqualTo(ExperienceErrorCode.NOT_FOUND);

            verify(experienceRepository, never()).delete(any());
        }
    }

    @Nested
    class GetDetailTest {

        @Test
        void getExperienceDetail_존재하는_경험이면_상세조회에_성공한다() {
            // given
            Experience experience = Experience.create(
                    userId,
                    "제목",
                    ExperienceType.PROJECT,
                    "내용",
                    LocalDate.of(2025, 1, 1),
                    LocalDate.of(2025, 1, 31)
            );

            when(experienceRepository.findByIdAndUserId(experienceId, userId))
                    .thenReturn(Optional.of(experience));

            // when
            ExperienceDetailResult result = experienceService.getExperienceDetail(experienceId, userId);

            // then
            assertThat(result).isNotNull();
            verify(experienceRepository).findByIdAndUserId(experienceId, userId);

            assertThat(result.title()).isEqualTo("제목");
            assertThat(result.experienceContent()).isEqualTo("내용");
        }

        @Test
        void getExperienceDetail_존재하지_않는_경험이면_NOT_FOUND_예외가_발생한다() {
            // given
            when(experienceRepository.findByIdAndUserId(experienceId, userId))
                    .thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> experienceService.getExperienceDetail(experienceId, userId))
                    .isInstanceOf(BaseException.class)
                    .extracting("errorCode")
                    .isEqualTo(ExperienceErrorCode.NOT_FOUND);
        }
    }

    @Nested
    class GetListTest {

        @Test
        void getExperiencesList_사용자의_경험목록을_반환한다() {
            // given
            Experience experience1 = Experience.create(
                    userId,
                    "경험1",
                    ExperienceType.PROJECT,
                    "내용1",
                    LocalDate.of(2025, 1, 1),
                    LocalDate.of(2025, 1, 31)
            );

            Experience experience2 = Experience.create(
                    userId,
                    "경험2",
                    ExperienceType.PROJECT,
                    "내용2",
                    LocalDate.of(2024, 7, 1),
                    LocalDate.of(2024, 12, 31)
            );

            when(experienceRepository.findAllByUserIdOrderByUpdatedAtDesc(userId))
                    .thenReturn(List.of(experience1, experience2));

            // when
            ExperienceListResult result = experienceService.getExperiencesList(userId);

            // then
            assertThat(result).isNotNull();
            verify(experienceRepository).findAllByUserIdOrderByUpdatedAtDesc(userId);
        }

        @Test
        void getExperiencesList_경험이_없으면_빈목록을_반환한다() {
            // given
            when(experienceRepository.findAllByUserIdOrderByUpdatedAtDesc(userId))
                    .thenReturn(Collections.emptyList());

            // when
            ExperienceListResult result = experienceService.getExperiencesList(userId);

            // then
            assertThat(result).isNotNull();
            verify(experienceRepository).findAllByUserIdOrderByUpdatedAtDesc(userId);
        }
    }
}