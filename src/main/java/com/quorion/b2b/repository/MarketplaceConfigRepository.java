package com.quorion.b2b.repository;

import com.quorion.b2b.model.tenant.MarketplaceConfig;
import com.quorion.b2b.model.tenant.MarketplaceMode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for MarketplaceConfig entity
 */
@Repository
public interface MarketplaceConfigRepository extends JpaRepository<MarketplaceConfig, UUID> {

    Optional<MarketplaceConfig> findByIsActiveTrue();

    List<MarketplaceConfig> findByIsActive(Boolean isActive);

    List<MarketplaceConfig> findByModeAndIsActive(MarketplaceMode mode, Boolean isActive);

    List<MarketplaceConfig> findByMode(MarketplaceMode mode);

    Optional<MarketplaceConfig> findFirstByIsActiveTrueOrderByCreatedAtDesc();
}
