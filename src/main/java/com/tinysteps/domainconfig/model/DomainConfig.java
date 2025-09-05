package com.tinysteps.domainconfig.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.Type;
import io.hypersistence.utils.hibernate.type.json.JsonType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "domain_configs")
public class DomainConfig {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @NotBlank
    @Column(unique = true)
    private String domainName;
    
    @NotBlank
    private String displayName;
    
    private String description;
    
    @NotNull
    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private DomainEntities entities;
    
    @NotNull
    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private DomainWorkflows workflows;
    
    @NotNull
    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private DomainTerminology terminology;
    
    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> customSettings;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getDomainName() { return domainName; }
    public void setDomainName(String domainName) { this.domainName = domainName; }
    
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public DomainEntities getEntities() { return entities; }
    public void setEntities(DomainEntities entities) { this.entities = entities; }
    
    public DomainWorkflows getWorkflows() { return workflows; }
    public void setWorkflows(DomainWorkflows workflows) { this.workflows = workflows; }
    
    public DomainTerminology getTerminology() { return terminology; }
    public void setTerminology(DomainTerminology terminology) { this.terminology = terminology; }
    
    public Map<String, Object> getCustomSettings() { return customSettings; }
    public void setCustomSettings(Map<String, Object> customSettings) { this.customSettings = customSettings; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Nested classes for JSON structure
    public static class DomainEntities {
        @JsonProperty("user_roles")
        private List<String> userRoles;
        
        @JsonProperty("context_type")
        private String contextType;
        
        @JsonProperty("transaction_type")
        private String transactionType;
        
        @JsonProperty("resource_type")
        private String resourceType;
        
        @JsonProperty("secondary_user_roles")
        private List<String> secondaryUserRoles;
        
        // Getters and Setters
        public List<String> getUserRoles() { return userRoles; }
        public void setUserRoles(List<String> userRoles) { this.userRoles = userRoles; }
        
        public String getContextType() { return contextType; }
        public void setContextType(String contextType) { this.contextType = contextType; }
        
        public String getTransactionType() { return transactionType; }
        public void setTransactionType(String transactionType) { this.transactionType = transactionType; }
        
        public String getResourceType() { return resourceType; }
        public void setResourceType(String resourceType) { this.resourceType = resourceType; }
        
        public List<String> getSecondaryUserRoles() { return secondaryUserRoles; }
        public void setSecondaryUserRoles(List<String> secondaryUserRoles) { this.secondaryUserRoles = secondaryUserRoles; }
    }
    
    public static class DomainWorkflows {
        @JsonProperty("transaction_states")
        private List<String> transactionStates;
        
        @JsonProperty("payment_required")
        private Boolean paymentRequired;
        
        @JsonProperty("location_required")
        private Boolean locationRequired;
        
        @JsonProperty("approval_workflow")
        private Boolean approvalWorkflow;
        
        @JsonProperty("rating_system")
        private Boolean ratingSystem;
        
        // Getters and Setters
        public List<String> getTransactionStates() { return transactionStates; }
        public void setTransactionStates(List<String> transactionStates) { this.transactionStates = transactionStates; }
        
        public Boolean getPaymentRequired() { return paymentRequired; }
        public void setPaymentRequired(Boolean paymentRequired) { this.paymentRequired = paymentRequired; }
        
        public Boolean getLocationRequired() { return locationRequired; }
        public void setLocationRequired(Boolean locationRequired) { this.locationRequired = locationRequired; }
        
        public Boolean getApprovalWorkflow() { return approvalWorkflow; }
        public void setApprovalWorkflow(Boolean approvalWorkflow) { this.approvalWorkflow = approvalWorkflow; }
        
        public Boolean getRatingSystem() { return ratingSystem; }
        public void setRatingSystem(Boolean ratingSystem) { this.ratingSystem = ratingSystem; }
    }
    
    public static class DomainTerminology {
        @JsonProperty("user_primary")
        private String userPrimary;
        
        @JsonProperty("user_secondary")
        private String userSecondary;
        
        @JsonProperty("context")
        private String context;
        
        @JsonProperty("transaction")
        private String transaction;
        
        @JsonProperty("resource")
        private String resource;
        
        @JsonProperty("context_plural")
        private String contextPlural;
        
        @JsonProperty("transaction_plural")
        private String transactionPlural;
        
        // Getters and Setters
        public String getUserPrimary() { return userPrimary; }
        public void setUserPrimary(String userPrimary) { this.userPrimary = userPrimary; }
        
        public String getUserSecondary() { return userSecondary; }
        public void setUserSecondary(String userSecondary) { this.userSecondary = userSecondary; }
        
        public String getContext() { return context; }
        public void setContext(String context) { this.context = context; }
        
        public String getTransaction() { return transaction; }
        public void setTransaction(String transaction) { this.transaction = transaction; }
        
        public String getResource() { return resource; }
        public void setResource(String resource) { this.resource = resource; }
        
        public String getContextPlural() { return contextPlural; }
        public void setContextPlural(String contextPlural) { this.contextPlural = contextPlural; }
        
        public String getTransactionPlural() { return transactionPlural; }
        public void setTransactionPlural(String transactionPlural) { this.transactionPlural = transactionPlural; }
    }
}