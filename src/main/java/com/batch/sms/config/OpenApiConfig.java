package com.batch.sms.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String BEARER_AUTH = "bearerAuth";

    @Bean
    public OpenAPI studentManagementOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Student Management System API")
                        .description("Department, Course, Student with relationships, pagination, search, "
                                + "caching, image upload, and JWT auth with ADMIN/STUDENT roles. "
                                + "POST /api/auth/login to get a token, then click Authorize and paste it "
                                + "(just the raw token — Swagger adds the 'Bearer ' prefix itself)")
                        .version("v1"))
                .components(new Components()
                        .addSecuritySchemes(BEARER_AUTH, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH));
    }
}
