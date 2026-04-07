package com.sogonsogon.gonggomoon.domain.file.api.request;

import com.sogonsogon.gonggomoon.domain.file.domain.DocumentCategory;
import jakarta.validation.constraints.NotNull;

public record UploadFileRequest(
        @NotNull(message = "파일 카테고리는 필수입니다.") DocumentCategory category
) {
}
