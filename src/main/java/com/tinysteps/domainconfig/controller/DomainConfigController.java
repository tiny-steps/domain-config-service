package com.tinysteps.domainconfig.controller;

import com.tinysteps.domainconfig.model.DomainConfig;
import com.tinysteps.domainconfig.service.DomainConfigService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/domain-config")
@CrossOrigin(origins = "*")
public class DomainConfigController {
    
    @Autowired
    private DomainConfigService domainConfigService;
    
    /**
     * Get domain configuration by domain name
     */
    @GetMapping("/{domainName}")
    public ResponseEntity<DomainConfig> getDomainConfig(@PathVariable String domainName) {
        Optional<DomainConfig> config = domainConfigService.getDomainConfig(domainName);
        return config.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Get all active domain configurations
     */
    @GetMapping
    public ResponseEntity<List<DomainConfig>> getAllDomainConfigs() {
        List<DomainConfig> configs = domainConfigService.getAllActiveDomainConfigs();
        return ResponseEntity.ok(configs);
    }
    
    /**
     * Create new domain configuration
     */
    @PostMapping
    public ResponseEntity<?> createDomainConfig(@Valid @RequestBody DomainConfig domainConfig) {
        try {
            // Check if domain already exists
            if (domainConfigService.domainExists(domainConfig.getDomainName())) {
                return ResponseEntity.badRequest()
                    .body("Domain '" + domainConfig.getDomainName() + "' already exists");
            }
            
            // Validate domain configuration
            if (!domainConfigService.validateDomainConfig(domainConfig)) {
                return ResponseEntity.badRequest()
                    .body("Invalid domain configuration. Please check required fields.");
            }
            
            DomainConfig savedConfig = domainConfigService.saveDomainConfig(domainConfig);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedConfig);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error creating domain configuration: " + e.getMessage());
        }
    }
    
    /**
     * Update existing domain configuration
     */
    @PutMapping("/{domainName}")
    public ResponseEntity<?> updateDomainConfig(
            @PathVariable String domainName, 
            @Valid @RequestBody DomainConfig domainConfig) {
        try {
            Optional<DomainConfig> existingConfig = domainConfigService.getDomainConfig(domainName);
            if (existingConfig.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            // Validate domain configuration
            if (!domainConfigService.validateDomainConfig(domainConfig)) {
                return ResponseEntity.badRequest()
                    .body("Invalid domain configuration. Please check required fields.");
            }
            
            // Update existing configuration
            DomainConfig existing = existingConfig.get();
            domainConfig.setId(existing.getId());
            domainConfig.setDomainName(domainName); // Ensure domain name consistency
            domainConfig.setCreatedAt(existing.getCreatedAt());
            
            DomainConfig updatedConfig = domainConfigService.saveDomainConfig(domainConfig);
            return ResponseEntity.ok(updatedConfig);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error updating domain configuration: " + e.getMessage());
        }
    }
    
    /**
     * Delete domain configuration (soft delete)
     */
    @DeleteMapping("/{domainName}")
    public ResponseEntity<?> deleteDomainConfig(@PathVariable String domainName) {
        try {
            Optional<DomainConfig> config = domainConfigService.getDomainConfig(domainName);
            if (config.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            domainConfigService.deleteDomainConfig(domainName);
            return ResponseEntity.ok().body("Domain configuration '" + domainName + "' deleted successfully");
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error deleting domain configuration: " + e.getMessage());
        }
    }
    
    /**
     * Get domain configurations by context type
     */
    @GetMapping("/by-context/{contextType}")
    public ResponseEntity<List<DomainConfig>> getDomainsByContextType(@PathVariable String contextType) {
        List<DomainConfig> configs = domainConfigService.getDomainsByContextType(contextType);
        return ResponseEntity.ok(configs);
    }
    
    /**
     * Get domains that require payment
     */
    @GetMapping("/payment-required")
    public ResponseEntity<List<DomainConfig>> getPaymentRequiredDomains() {
        List<DomainConfig> configs = domainConfigService.getPaymentRequiredDomains();
        return ResponseEntity.ok(configs);
    }
    
    /**
     * Get domain configurations by transaction type
     */
    @GetMapping("/by-transaction/{transactionType}")
    public ResponseEntity<List<DomainConfig>> getDomainsByTransactionType(@PathVariable String transactionType) {
        List<DomainConfig> configs = domainConfigService.getDomainsByTransactionType(transactionType);
        return ResponseEntity.ok(configs);
    }
    
    /**
     * Validate domain configuration
     */
    @PostMapping("/validate")
    public ResponseEntity<?> validateDomainConfig(@Valid @RequestBody DomainConfig domainConfig) {
        try {
            boolean isValid = domainConfigService.validateDomainConfig(domainConfig);
            if (isValid) {
                return ResponseEntity.ok().body("Domain configuration is valid");
            } else {
                return ResponseEntity.badRequest()
                    .body("Invalid domain configuration. Please check required fields.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error validating domain configuration: " + e.getMessage());
        }
    }
    
    /**
     * Clear all caches
     */
    @PostMapping("/clear-cache")
    public ResponseEntity<String> clearCache() {
        try {
            domainConfigService.clearAllCaches();
            return ResponseEntity.ok("All caches cleared successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error clearing caches: " + e.getMessage());
        }
    }
    
    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Domain Configuration Service is running");
    }
}