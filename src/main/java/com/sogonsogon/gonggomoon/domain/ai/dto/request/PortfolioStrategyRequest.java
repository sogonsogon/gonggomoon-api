package com.sogonsogon.gonggomoon.domain.ai.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sogonsogon.gonggomoon.domain.experience.domain.Experience;
import java.util.List;

/**
 * 포트폴리오 전략 생성 작업을 AI 워커(/tasks/execute)로 전달하기 위한 메시지 바디.
 * 워커는 job_type으로 작업을 분기하며, DB 조회 없이 이 메시지의 인라인 데이터만으로 처리한다.
 * 따라서 경험 목록과 공고 분석 결과(title, summary)를 모두 인라인으로 담아 보낸다.
 * id(portfolio_strategy_id)는 콜백의 최상위 id로 에코백되어 대상 전략을 식별한다.
 */
public record PortfolioStrategyRequest(
    @JsonProperty("id")
    Long id,

    @JsonProperty("job_type")
    String jobType,

    @JsonProperty("user_id")
    Long userId,

    @JsonProperty("callback_url")
    String callbackUrl,

    List<ExperienceInput> experiences,

    @JsonProperty("position_type")
    String positionType,

    @JsonProperty("industry_type")
    String industryType,

    @JsonProperty("post_analysis")
    PostAnalysisInput postAnalysis
) {

    /**
     * 워커의 ExperienceInput 계약(camelCase 필드)에 맞춘 경험 항목.
     * enum/날짜는 워커가 문자열로 받으므로 name()/ISO 문자열로 변환한다.
     */
    public record ExperienceInput(
        String title,
        String experienceType,
        String experienceContent,
        int teamSize,
        String startDate,
        String endDate,
        String roleType,
        String impactTier
    ) {
        public static ExperienceInput from(Experience experience) {
            return new ExperienceInput(
                experience.getTitle(),
                experience.getExperienceType() != null ? experience.getExperienceType().name() : null,
                experience.getExperienceContent(),
                experience.getTeamSize(),
                experience.getStartDate() != null ? experience.getStartDate().toString() : null,
                experience.getEndDate() != null ? experience.getEndDate().toString() : null,
                experience.getRoleType() != null ? experience.getRoleType().name() : null,
                experience.getImpactTier() != null ? experience.getImpactTier().name() : null
            );
        }
    }

    /**
     * 워커의 PostAnalysisInput 계약. 공고 분석 결과를 인라인으로 전달한다.
     */
    public record PostAnalysisInput(
        String title,
        String summary
    ) {
    }
}
