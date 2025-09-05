package com.tinysteps.domainconfig.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableCaching
public class ApplicationConfig {
    
    /**
     * Configure ObjectMapper for JSON serialization/deserialization
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        
        // Register JavaTimeModule for LocalDateTime support
        mapper.registerModule(new JavaTimeModule());
        
        // Disable writing dates as timestamps
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        
        // Use camelCase property naming
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE);
        
        // Ignore unknown properties during deserialization
        mapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        // Pretty print JSON in development
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        
        return mapper;
    }
    
    /**
     * Configure Caffeine Cache Manager
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        
        // Set cache names
        cacheManager.setCacheNames(Arrays.asList(
            "domainConfigs",
            "allDomainConfigs",
            "domainsByContext",
            "paymentRequiredDomains",
            "domainsByTransaction"
        ));
        
        // Configure cache specification
        cacheManager.setCacheSpecification("maximumSize=1000,expireAfterWrite=1h");
        
        return cacheManager;
    }
    
    /**
     * Configure CORS for cross-origin requests
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Allow all origins in development (restrict in production)
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        
        // Allow common HTTP methods
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));
        
        // Allow common headers
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization", "Content-Type", "X-Requested-With", 
            "Accept", "Origin", "Access-Control-Request-Method", 
            "Access-Control-Request-Headers", "X-Domain-Name"
        ));
        
        // Expose headers that clients can access
        configuration.setExposedHeaders(Arrays.asList(
            "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials",
            "X-Total-Count", "X-Domain-Name"
        ));
        
        // Allow credentials
        configuration.setAllowCredentials(true);
        
        // Cache preflight requests for 1 hour
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
    
    /**
     * Configure custom properties
     */
    @Bean
    public DomainConfigProperties domainConfigProperties() {
        return new DomainConfigProperties();
    }
    
    /**
     * Custom properties class for domain configuration settings
     */
    public static class DomainConfigProperties {
        private long defaultCacheTtl = 3600; // 1 hour
        private boolean strictValidationMode = true;
        private String[] supportedDomains = {"healthcare", "ecommerce", "cab-booking"};
        
        // Getters and setters
        public long getDefaultCacheTtl() {
            return defaultCacheTtl;
        }
        
        public void setDefaultCacheTtl(long defaultCacheTtl) {
            this.defaultCacheTtl = defaultCacheTtl;
        }
        
        public boolean isStrictValidationMode() {
            return strictValidationMode;
        }
        
        public void setStrictValidationMode(boolean strictValidationMode) {
            this.strictValidationMode = strictValidationMode;
        }
        
        public String[] getSupportedDomains() {
            return supportedDomains;
        }
        
        public void setSupportedDomains(String[] supportedDomains) {
            this.supportedDomains = supportedDomains;
        }
    }
}