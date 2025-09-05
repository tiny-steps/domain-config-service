package com.tinysteps.domainconfig.service;

import com.tinysteps.domainconfig.model.DomainConfig;
import com.tinysteps.domainconfig.repository.DomainConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DomainConfigService {
    
    @Autowired
    private DomainConfigRepository domainConfigRepository;
    
    /**
     * Get domain configuration by domain name (cached)
     */
    @Cacheable(value = "domainConfigs", key = "#domainName")
    public Optional<DomainConfig> getDomainConfig(String domainName) {
        return domainConfigRepository.findByDomainNameAndIsActiveTrue(domainName);
    }
    
    /**
     * Get all active domain configurations (cached)
     */
    @Cacheable(value = "allDomainConfigs")
    public List<DomainConfig> getAllActiveDomainConfigs() {
        return domainConfigRepository.findByIsActiveTrueOrderByDomainName();
    }
    
    /**
     * Create or update domain configuration
     */
    @CacheEvict(value = {"domainConfigs", "allDomainConfigs"}, allEntries = true)
    public DomainConfig saveDomainConfig(DomainConfig domainConfig) {
        return domainConfigRepository.save(domainConfig);
    }
    
    /**
     * Delete domain configuration (soft delete)
     */
    @CacheEvict(value = {"domainConfigs", "allDomainConfigs"}, allEntries = true)
    public void deleteDomainConfig(String domainName) {
        Optional<DomainConfig> config = domainConfigRepository.findByDomainNameAndIsActiveTrue(domainName);
        if (config.isPresent()) {
            DomainConfig domainConfig = config.get();
            domainConfig.setIsActive(false);
            domainConfigRepository.save(domainConfig);
        }
    }
    
    /**
     * Check if domain exists
     */
    public boolean domainExists(String domainName) {
        return domainConfigRepository.existsByDomainName(domainName);
    }
    
    /**
     * Get domain configurations by context type
     */
    @Cacheable(value = "domainsByContext", key = "#contextType")
    public List<DomainConfig> getDomainsByContextType(String contextType) {
        return domainConfigRepository.findByContextType(contextType);
    }
    
    /**
     * Get domains that require payment
     */
    @Cacheable(value = "paymentRequiredDomains")
    public List<DomainConfig> getPaymentRequiredDomains() {
        return domainConfigRepository.findPaymentRequiredDomains();
    }
    
    /**
     * Get domain configurations by transaction type
     */
    @Cacheable(value = "domainsByTransaction", key = "#transactionType")
    public List<DomainConfig> getDomainsByTransactionType(String transactionType) {
        return domainConfigRepository.findByTransactionType(transactionType);
    }
    
    /**
     * Validate domain configuration
     */
    public boolean validateDomainConfig(DomainConfig domainConfig) {
        // Basic validation logic
        if (domainConfig.getDomainName() == null || domainConfig.getDomainName().trim().isEmpty()) {
            return false;
        }
        
        if (domainConfig.getEntities() == null || 
            domainConfig.getWorkflows() == null || 
            domainConfig.getTerminology() == null) {
            return false;
        }
        
        // Validate required fields in entities
        DomainConfig.DomainEntities entities = domainConfig.getEntities();
        if (entities.getUserRoles() == null || entities.getUserRoles().isEmpty() ||
            entities.getContextType() == null || entities.getContextType().trim().isEmpty() ||
            entities.getTransactionType() == null || entities.getTransactionType().trim().isEmpty()) {
            return false;
        }
        
        // Validate required fields in workflows
        DomainConfig.DomainWorkflows workflows = domainConfig.getWorkflows();
        if (workflows.getTransactionStates() == null || workflows.getTransactionStates().isEmpty()) {
            return false;
        }
        
        // Validate required fields in terminology
        DomainConfig.DomainTerminology terminology = domainConfig.getTerminology();
        if (terminology.getUserPrimary() == null || terminology.getUserPrimary().trim().isEmpty() ||
            terminology.getContext() == null || terminology.getContext().trim().isEmpty() ||
            terminology.getTransaction() == null || terminology.getTransaction().trim().isEmpty()) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Clear all caches
     */
    @CacheEvict(value = {"domainConfigs", "allDomainConfigs", "domainsByContext", 
                        "paymentRequiredDomains", "domainsByTransaction"}, allEntries = true)
    public void clearAllCaches() {
        // Cache cleared
    }
}