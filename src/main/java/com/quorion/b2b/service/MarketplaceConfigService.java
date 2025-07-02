package com.quorion.b2b.service;

import com.quorion.b2b.model.tenant.MarketplaceConfig;
import com.quorion.b2b.model.tenant.MarketplaceMode;
import com.quorion.b2b.repository.MarketplaceConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * MarketplaceConfig Service
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MarketplaceConfigService {

    private final MarketplaceConfigRepository marketplaceConfigRepository;

    @Transactional(readOnly = true)
    public List<MarketplaceConfig> getAllConfigs(MarketplaceMode mode, Boolean isActive) {
        if (mode != null && isActive != null) {
            return marketplaceConfigRepository.findByModeAndIsActive(mode, isActive);
        } else if (mode != null) {
            return marketplaceConfigRepository.findByMode(mode);
        } else if (isActive != null) {
            return marketplaceConfigRepository.findByIsActive(isActive);
        }
        return marketplaceConfigRepository.findAll();
    }

    @Transactional(readOnly = true)
    public MarketplaceConfig getConfigById(UUID id) {
        return marketplaceConfigRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Marketplace config not found"));
    }

    @Transactional(readOnly = true)
    public MarketplaceConfig getActiveConfig() {
        return marketplaceConfigRepository.findFirstByIsActiveTrueOrderByCreatedAtDesc()
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("No active marketplace configuration found"));
    }

    @Transactional
    public MarketplaceConfig createConfig(MarketplaceConfig config) {
        return marketplaceConfigRepository.save(config);
    }

    @Transactional
    public MarketplaceConfig updateConfig(UUID id, MarketplaceConfig details) {
        MarketplaceConfig config = getConfigById(id);
        if (details.getMode() != null) config.setMode(details.getMode());
        if (details.getAllowDirectPurchase() != null) config.setAllowDirectPurchase(details.getAllowDirectPurchase());
        if (details.getRequireQuoteApproval() != null) config.setRequireQuoteApproval(details.getRequireQuoteApproval());
        if (details.getAllowGuestCheckout() != null) config.setAllowGuestCheckout(details.getAllowGuestCheckout());
        if (details.getMinOrderValue() != null) config.setMinOrderValue(details.getMinOrderValue());
        if (details.getIsActive() != null) config.setIsActive(details.getIsActive());
        return marketplaceConfigRepository.save(config);
    }

    @Transactional
    public MarketplaceConfig activateConfig(UUID id) {
        // Deactivate all other configs
        marketplaceConfigRepository.findByIsActive(true).forEach(c -> {
            c.setIsActive(false);
            marketplaceConfigRepository.save(c);
        });

        // Activate this config
        MarketplaceConfig config = getConfigById(id);
        config.setIsActive(true);
        return marketplaceConfigRepository.save(config);
    }

    @Transactional
    public void deleteConfig(UUID id) {
        marketplaceConfigRepository.deleteById(id);
    }
}
