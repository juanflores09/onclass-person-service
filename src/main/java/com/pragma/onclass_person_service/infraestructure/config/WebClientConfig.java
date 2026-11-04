package com.pragma.onclass_person_service.infraestructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean(name = "bootcampWebClient")
    public WebClient bootcampWebClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8082")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean(name = "reportWebClient")
    public WebClient reportWebClient() {
        return WebClient.builder()
                .baseUrl("http://localhost:8084")
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
