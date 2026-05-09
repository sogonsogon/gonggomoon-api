package com.sogonsogon.gonggomoon.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.s3")
public class S3Properties {

    /**
     * S3 bucket name
     */
    private String bucket;

    /**
     * AWS region
     * ex) ap-northeast-2
     */
    private String region;

    /**
     * AWS secret key
     */
    private String secretKey;

    private String accessKey;
}
