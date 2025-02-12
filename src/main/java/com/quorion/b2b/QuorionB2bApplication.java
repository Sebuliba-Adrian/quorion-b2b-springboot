package com.quorion.b2b;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Quorion B2B API - Main Application
 *
 * A comprehensive B2B distributor buyer-seller negotiation system with:
 * - Multi-tenant architecture
 * - Shopping cart with soft delete
 * - Lead management with state machine
 * - Quote negotiation with state machine
 * - Order processing with state machine
 * - Product and SKU management
 * - Price tiers (volume-based pricing)
 * - Marketplace configuration with feature flags
 * - Distributor integration
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableCaching
public class QuorionB2bApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuorionB2bApplication.class, args);
    }
}
