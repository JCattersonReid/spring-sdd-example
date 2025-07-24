package com.example.springsddexample.config;

import com.example.springsddexample.controller.ControllerErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ControllerErrorHandler controllerErrorHandler() {
        return new ControllerErrorHandler();
    }
}