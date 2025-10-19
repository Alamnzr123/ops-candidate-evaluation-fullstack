package com.ops_candidate_evaluation.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Ops Candidate Evaluation API")
                        .version("1.0.0")
                        .description("REST API for Ops Candidate Evaluation (backend).")
                        .contact(new Contact().name("Rahmad Alamsyah Nazaruddin").email("nzr.rahmad@gmail.com")));
    }
}
