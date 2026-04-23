package com.sogonsogon.gonggomoon.domain.file.infrastructure.s3;

import java.net.URI;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@EnableConfigurationProperties(S3Properties.class)
@ConditionalOnProperty(name = "app.file.storage-type", havingValue = "s3")
public class S3Config {

    @Bean
    public S3Client s3Client(S3Properties s3Properties) {
        return S3Client.builder()
                .region(Region.of(s3Properties.getRegion()))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(
                                        s3Properties.getAccessKey(),
                                        s3Properties.getSecretKey()
                                )
                        )
                )
                // 로컬에서 MinIO를 사용할 때는 endpoint를 명시적으로 설정해야 합니다.
                .endpointOverride(URI.create(s3Properties.getEndpoint()))
                .forcePathStyle(true)
                .build();
    }
}
