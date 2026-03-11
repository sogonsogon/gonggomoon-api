package com.sogonsogon.gonggomoon.domain.post.dto.request;

import com.sogonsogon.gonggomoon.domain.strategy.domain.JobType;
import jakarta.validation.constraints.Size;

public record SearchPostRequest(

        JobType jobType,

        @Size(max = 50, message = "검색어는 50자 이하입니다.")
        String title
) {
}
