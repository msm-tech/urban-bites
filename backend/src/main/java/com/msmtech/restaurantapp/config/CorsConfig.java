package com.msmtech.restaurantapp.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    // @Value injects properties from application.properties
    @Value("${app.cors.allowed-origins:http://localhost:3000}")
    private String allowedOrigins; // Store as single string

    @Value("${app.cors.allowed-methods:GET,POST,PUT,DELETE}")
    private String allowedMethods;

    @Value("${app.cors.allowed-headers:*}")
    private String allowedHeaders;

    @Value("${app.cors.allow-credentials:true}")
    private boolean allowCredentials;

    private static final Logger logger = LoggerFactory.getLogger(CorsConfig.class);

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Split comma-separated values into arrays
        String[] originsArray = allowedOrigins.split(",");
        String[] methodsArray = allowedMethods.split(",");

        logger.debug("Configuring CORS with origins={} methods={} headers={} credentials={}", allowedOrigins, allowedMethods, allowedHeaders, allowCredentials);

        registry.addMapping("/api/**")
                .allowedOrigins(originsArray)
                .allowedMethods(methodsArray)
                .allowedHeaders(allowedHeaders)
                .allowCredentials(allowCredentials);
    }
}
