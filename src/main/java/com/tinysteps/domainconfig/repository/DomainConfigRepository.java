package com.tinysteps.domainconfig.repository;

import com.tinysteps.domainconfig.model.DomainConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DomainConfigRepository extends JpaRepository<DomainConfig, String> {
    
    /**
     * Find domain configuration by domain name
     */
    Optional<DomainConfig> findByDomainNameAndIsActiveTrue(String domainName);
    
    /**
     * Find all active domain configurations
     */
    List<DomainConfig> findByIsActiveTrueOrderByDomainName();
    
    /**
     * Check if domain name exists
     */
    boolean existsByDomainName(String domainName);
    
    /**
     * Find domain configurations by context type
     */
    @Query("SELECT dc FROM DomainConfig dc WHERE dc.isActive = true AND JSON_EXTRACT(dc.entities, '$.context_type') = :contextType")
    List<DomainConfig> findByContextType(@Param("contextType") String contextType);
    
    /**
     * Find domain configurations that require payment
     */
    @Query("SELECT dc FROM DomainConfig dc WHERE dc.isActive = true AND JSON_EXTRACT(dc.workflows, '$.payment_required') = true")
    List<DomainConfig> findPaymentRequiredDomains();
    
    /**
     * Find domain configurations by transaction type
     */
    @Query("SELECT dc FROM DomainConfig dc WHERE dc.isActive = true AND JSON_EXTRACT(dc.entities, '$.transaction_type') = :transactionType")
    List<DomainConfig> findByTransactionType(@Param("transactionType") String transactionType);
}