package com.sogonsogon.gonggomoon.domain.ai.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sogonsogon.gonggomoon.domain.experience.domain.Experience;
import java.util.List;

/**
 * 포트폴리오 전략 생성 작업을 AI 워커(/tasks/execute)로 전달하기 위한 메시지 바디.
 * 워커는 DB 조회 없이 메시지에 인라인된 경험/공고 분석 데이터만으로 전략을 생성한다.
 * id(포트폴리오 전략 ID)는 콜백의 최상위 id로 에코백된다.
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

    List<Experience> experiences,

    @JsonProperty("position_type")
    String positionType,

    @JsonProperty("industry_type")
    String industryType,

    @JsonProperty("post_analysis")
    PostAnalysisInput postAnalysis
) {

    /**
     * 워커가 전략 생성에 참고할 공고 분석 결과 요약.
     */
    public record PostAnalysisInput(
        String title,
        String summary
    ) {
    }
}
