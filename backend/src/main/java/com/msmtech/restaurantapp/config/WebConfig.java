package com.msmtech.restaurantapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
//CORS
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")                // Apply to all endpoints
                .allowedOrigins("http://localhost:3000")        // React app URL
                .allowedMethods("GET","POST","PUT","DELETE")    // Allowed HTTP methods
                .allowedHeaders("*")                            // All headers allowed
                .allowCredentials(true);                        // Allow cookies if needed
    }

}
