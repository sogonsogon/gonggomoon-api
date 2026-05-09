package com.sogonsogon.gonggomoon.domain.ai.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sogonsogon.gonggomoon.domain.experience.domain.Experience;
import java.util.List;

public record PortfolioStrategyRequest(
    @JsonProperty("user_id")
    Long userId,

    @JsonProperty("portfolio_strategy_id")
    Long portfolioStrategyId,

    List<Experience> experiences,

    @JsonProperty("position_type")
    String positionType,

    @JsonProperty("industry_type")
    String industryType
) {


}
