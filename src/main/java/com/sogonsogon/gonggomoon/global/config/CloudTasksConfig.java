package com.sogonsogon.gonggomoon.global.config;

import com.google.cloud.tasks.v2.CloudTasksClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class CloudTasksConfig {

    /*
     * Cloud Tasks 클라이언트 빈
     * 인증은 ADC(Application Default Credentials)를 사용합니다.
     * - 로컬: GOOGLE_APPLICATION_CREDENTIALS 환경변수 또는 gcloud auth application-default login
     * - Cloud Run: 실행 서비스 계정 자동 사용
     * */
    @Bean(destroyMethod = "close")
    public CloudTasksClient cloudTasksClient() throws IOException {
        return CloudTasksClient.create();
    }
}
