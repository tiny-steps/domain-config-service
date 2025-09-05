package com.tinysteps.domainconfig.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinysteps.domainconfig.model.DomainConfig;
import com.tinysteps.domainconfig.repository.DomainConfigRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class DataInitializationService {
    
    private static final Logger logger = LoggerFactory.getLogger(DataInitializationService.class);
    
    @Autowired
    private DomainConfigRepository domainConfigRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private final List<String> sampleConfigFiles = Arrays.asList(
        "sample-configs/healthcare-config.json",
        "sample-configs/ecommerce-config.json",
        "sample-configs/cab-booking-config.json"
    );
    
    @PostConstruct
    public void initializeData() {
        logger.info("Starting domain configuration data initialization...");
        
        try {
            loadSampleConfigurations();
            logger.info("Domain configuration data initialization completed successfully");
        } catch (Exception e) {
            logger.error("Error during domain configuration data initialization: {}", e.getMessage(), e);
        }
    }
    
    private void loadSampleConfigurations() {
        for (String configFile : sampleConfigFiles) {
            try {
                loadConfigurationFromFile(configFile);
            } catch (Exception e) {
                logger.error("Error loading configuration from file {}: {}", configFile, e.getMessage(), e);
            }
        }
    }
    
    private void loadConfigurationFromFile(String configFile) throws IOException {
        logger.info("Loading configuration from file: {}", configFile);
        
        ClassPathResource resource = new ClassPathResource(configFile);
        if (!resource.exists()) {
            logger.warn("Configuration file {} does not exist, skipping...", configFile);
            return;
        }
        
        try (InputStream inputStream = resource.getInputStream()) {
            DomainConfig domainConfig = objectMapper.readValue(inputStream, DomainConfig.class);
            
            // Check if configuration already exists
            if (domainConfigRepository.existsByDomainName(domainConfig.getDomainName())) {
                logger.info("Domain configuration '{}' already exists, skipping initialization", 
                    domainConfig.getDomainName());
                return;
            }
            
            // Set timestamps
            LocalDateTime now = LocalDateTime.now();
            domainConfig.setCreatedAt(now);
            domainConfig.setUpdatedAt(now);
            
            // Validate configuration before saving
            if (validateConfiguration(domainConfig)) {
                domainConfigRepository.save(domainConfig);
                logger.info("Successfully loaded domain configuration: {}", domainConfig.getDomainName());
            } else {
                logger.error("Invalid configuration in file: {}", configFile);
            }
        }
    }
    
    private boolean validateConfiguration(DomainConfig config) {
        if (config.getDomainName() == null || config.getDomainName().trim().isEmpty()) {
            logger.error("Domain name is required");
            return false;
        }
        
        if (config.getEntities() == null) {
            logger.error("Entities configuration is required");
            return false;
        }
        
        if (config.getWorkflows() == null) {
            logger.error("Workflows configuration is required");
            return false;
        }
        
        if (config.getTerminology() == null) {
            logger.error("Terminology configuration is required");
            return false;
        }
        
        // Validate entities
        DomainConfig.DomainEntities entities = config.getEntities();
        if (entities.getUserRoles() == null || entities.getUserRoles().isEmpty()) {
            logger.error("User roles are required");
            return false;
        }
        
        if (entities.getContextType() == null || entities.getContextType().trim().isEmpty()) {
            logger.error("Context type is required");
            return false;
        }
        
        if (entities.getTransactionType() == null || entities.getTransactionType().trim().isEmpty()) {
            logger.error("Transaction type is required");
            return false;
        }
        
        // Validate workflows
        DomainConfig.DomainWorkflows workflows = config.getWorkflows();
        if (workflows.getTransactionStates() == null || workflows.getTransactionStates().isEmpty()) {
            logger.error("Transaction states are required");
            return false;
        }
        
        // Validate terminology
        DomainConfig.DomainTerminology terminology = config.getTerminology();
        if (terminology.getUserPrimary() == null || terminology.getUserPrimary().trim().isEmpty()) {
            logger.error("Primary user terminology is required");
            return false;
        }
        
        if (terminology.getContext() == null || terminology.getContext().trim().isEmpty()) {
            logger.error("Context terminology is required");
            return false;
        }
        
        if (terminology.getTransaction() == null || terminology.getTransaction().trim().isEmpty()) {
            logger.error("Transaction terminology is required");
            return false;
        }
        
        return true;
    }
    
    /**
     * Reload all configurations (useful for development/testing)
     */
    public void reloadConfigurations() {
        logger.info("Reloading all domain configurations...");
        
        // Delete existing configurations
        domainConfigRepository.deleteAll();
        
        // Reload from files
        loadSampleConfigurations();
        
        logger.info("Domain configurations reloaded successfully");
    }
    
    /**
     * Get initialization status
     */
    public boolean isInitialized() {
        long count = domainConfigRepository.count();
        return count >= sampleConfigFiles.size();
    }
    
    /**
     * Get loaded domain count
     */
    public long getLoadedDomainCount() {
        return domainConfigRepository.count();
    }
}