package com.msmtech.restaurantapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Split comma-separated values into arrays
        String[] originsArray = allowedOrigins.split(",");
        String[] methodsArray = allowedMethods.split(",");

        System.out.println("Configuring CORS with:");
        System.out.println(" - Origins: " + String.join(", ", allowedOrigins));
        System.out.println(" - Methods: " + String.join(", ", allowedMethods));
        System.out.println(" - Headers: " + allowedHeaders);
        System.out.println(" - Credentials: " + allowCredentials);

        registry.addMapping("/api/**")
                .allowedOrigins(originsArray)
                .allowedMethods(methodsArray)
                .allowedHeaders(allowedHeaders)
                .allowCredentials(allowCredentials);

//        // More specific mapping example
//        registry.addMapping("/auth/**")
//                .allowedOrigins(allowedOrigins)
//                .allowedMethods("POST","OPTIONS")
//                .allowCredentials(allowCredentials);
    }
}
