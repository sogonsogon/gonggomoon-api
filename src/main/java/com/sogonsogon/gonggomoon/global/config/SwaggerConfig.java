package com.sogonsogon.gonggomoon.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SwaggerConfig implements WebMvcConfigurer {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    // /swagger-ui, /swagger-ui/ 로 접근해도 정식 진입점(/swagger-ui/index.html)으로 리다이렉트.
    // (이 매핑이 없으면 매칭 리소스가 없어 NoResourceFoundException → 글로벌 핸들러가 500으로 변환됨)
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/swagger-ui", "/swagger-ui/index.html");
        registry.addRedirectViewController("/swagger-ui/", "/swagger-ui/index.html");
    }

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
            .title("Gonggomoon API")
            .description("공고문 서비스 API 문서")
            .version("v1");

        // JWT Bearer 인증 스킴 (Authorization: Bearer <token>)
        SecurityScheme securityScheme = new SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
            .in(SecurityScheme.In.HEADER)
            .name("Authorization");

        SecurityRequirement securityRequirement = new SecurityRequirement().addList(SECURITY_SCHEME_NAME);

        return new OpenAPI()
            .info(info)
            .addSecurityItem(securityRequirement)
            .components(new Components().addSecuritySchemes(SECURITY_SCHEME_NAME, securityScheme));
    }
}
