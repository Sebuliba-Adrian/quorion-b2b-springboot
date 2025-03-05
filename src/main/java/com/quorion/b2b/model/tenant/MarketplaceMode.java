package com.quorion.b2b.model.tenant;

/**
 * Different marketplace operation modes
 */
public enum MarketplaceMode {
    B2B_NEGOTIATION,      // Lead → Quote → Order
    DIRECT_MARKETPLACE,   // Cart → Order
    HYBRID,               // Both B2B and Direct
    MULTI_VENDOR          // Multi-Vendor Marketplace
}
