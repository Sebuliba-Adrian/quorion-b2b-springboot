package com.quorion.b2b.service;

import com.quorion.b2b.model.tenant.MarketplaceConfig;
import com.quorion.b2b.model.tenant.SellerMarketplace;
import com.quorion.b2b.repository.SellerMarketplaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * SellerMarketplace Service
 */
@Service
@RequiredArgsConstructor
public class SellerMarketplaceService {

    private final SellerMarketplaceRepository sellerMarketplaceRepository;
    private final MarketplaceConfigService marketplaceConfigService;

    @Transactional(readOnly = true)
    public List<SellerMarketplace> getAllSellerMarketplaces(UUID sellerId, Boolean isActive) {
        if (sellerId != null && isActive != null) {
            return sellerMarketplaceRepository.findBySellerIdAndIsActive(sellerId, isActive);
        } else if (sellerId != null) {
            return sellerMarketplaceRepository.findBySellerId(sellerId);
        } else if (isActive != null) {
            return sellerMarketplaceRepository.findByIsActive(isActive);
        }
        return sellerMarketplaceRepository.findAll();
    }

    @Transactional(readOnly = true)
    public SellerMarketplace getSellerMarketplaceById(UUID id) {
        return sellerMarketplaceRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Seller marketplace not found"));
    }

    @Transactional
    public SellerMarketplace createSellerMarketplace(SellerMarketplace sellerMarketplace) {
        return sellerMarketplaceRepository.save(sellerMarketplace);
    }

    @Transactional
    public SellerMarketplace updateSellerMarketplace(UUID id, SellerMarketplace details) {
        SellerMarketplace sellerMarketplace = getSellerMarketplaceById(id);
        if (details.getAllowDirectPurchase() != null) sellerMarketplace.setAllowDirectPurchase(details.getAllowDirectPurchase());
        if (details.getRequireQuoteApproval() != null) sellerMarketplace.setRequireQuoteApproval(details.getRequireQuoteApproval());
        if (details.getMinOrderValue() != null) sellerMarketplace.setMinOrderValue(details.getMinOrderValue());
        if (details.getAutoAcceptOrders() != null) sellerMarketplace.setAutoAcceptOrders(details.getAutoAcceptOrders());
        if (details.getAllowNegotiations() != null) sellerMarketplace.setAllowNegotiations(details.getAllowNegotiations());
        if (details.getIsActive() != null) sellerMarketplace.setIsActive(details.getIsActive());
        return sellerMarketplaceRepository.save(sellerMarketplace);
    }

    @Transactional
    public void deleteSellerMarketplace(UUID id) {
        if (!sellerMarketplaceRepository.existsById(id)) {
            throw new jakarta.persistence.EntityNotFoundException("SellerMarketplace not found with id: " + id);
        }
        sellerMarketplaceRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getEffectiveSettings(UUID id) {
        SellerMarketplace sellerMarketplace = getSellerMarketplaceById(id);
        MarketplaceConfig globalConfig = null;

        try {
            globalConfig = marketplaceConfigService.getActiveConfig();
        } catch (Exception e) {
            // No active config
        }

        Map<String, Object> effectiveSettings = new HashMap<>();

        // Get effective settings with fallback to global config
        effectiveSettings.put("allow_direct_purchase", getEffectiveBoolean(
                sellerMarketplace.getAllowDirectPurchase(),
                globalConfig != null ? globalConfig.getAllowDirectPurchase() : true
        ));

        effectiveSettings.put("require_quote_approval", getEffectiveBoolean(
                sellerMarketplace.getRequireQuoteApproval(),
                globalConfig != null ? globalConfig.getRequireQuoteApproval() : false
        ));

        effectiveSettings.put("min_order_value",
                sellerMarketplace.getMinOrderValue() != null
                        ? sellerMarketplace.getMinOrderValue()
                        : (globalConfig != null ? globalConfig.getMinOrderValue() : BigDecimal.ZERO)
        );

        effectiveSettings.put("auto_accept_orders", sellerMarketplace.getAutoAcceptOrders());
        effectiveSettings.put("allow_negotiations", sellerMarketplace.getAllowNegotiations());

        return effectiveSettings;
    }

    private Boolean getEffectiveBoolean(Boolean sellerValue, Boolean globalValue) {
        return sellerValue != null ? sellerValue : globalValue;
    }
}
