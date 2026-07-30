package com.optinova.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

/**
 * Cross-Origin Resource Sharing (CORS) Configuration.
 * Configures origin rules, methods, and headers to enable full seamless connectivity with the existing frontend application.
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        
        // Allow requests from all origins (can be restricted in production)
        config.setAllowedOriginPatterns(List.of("*"));
        
        // Allowed HTTP Methods
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        
        // Allowed Request Headers
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With", "Accept", "Origin"));
        
        // Exposed Headers in Response
        config.setExposedHeaders(List.of("Authorization"));
        
        // Allow credentials (cookies/auth headers)
        config.setAllowCredentials(true);
        
        // Cache pre-flight response for 1 hour
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
