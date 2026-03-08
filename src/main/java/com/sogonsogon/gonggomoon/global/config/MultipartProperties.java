package com.sogonsogon.gonggomoon.global.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.unit.DataSize;

@Getter
@Component
@ConfigurationProperties(prefix = "spring.servlet.multipart")
public class MultipartProperties {

    private DataSize maxFileSize;

    // Spring이 yml의 max-file-size 값을 읽어 DataSize 타입으로 변환한 뒤 이 필드에 바인딩한다.
    public void setMaxFileSize(DataSize maxFileSize) {
        this.maxFileSize = maxFileSize;
    }
}
