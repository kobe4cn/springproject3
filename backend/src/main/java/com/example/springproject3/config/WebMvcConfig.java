package com.example.springproject3.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Value("#{'${cors.allowed-origins}'.split(',')}")
    private List<String> allowOrigins;
    @Value("#{'${cors.allowed-methods}'.split(',')}")
    private List<String> allowMethods;


    @Override
    public void addCorsMappings(CorsRegistry registry) {
        CorsRegistration corsRegistration = registry.addMapping("/api/**");
        corsRegistration.allowedHeaders(CorsConfiguration.ALL);
        allowOrigins.forEach(corsRegistration::allowedOrigins);
        allowMethods.forEach(corsRegistration::allowedMethods);


    }
}
