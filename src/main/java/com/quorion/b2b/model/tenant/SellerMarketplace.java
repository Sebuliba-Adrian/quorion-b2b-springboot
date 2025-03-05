package com.quorion.b2b.model.tenant;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.quorion.b2b.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

/**
 * Individual seller marketplace/storefront configuration
 * Allows each seller to have their own marketplace settings
 */
@Entity
@Table(name = "seller_marketplace")
@Data
@EqualsAndHashCode(callSuper = true, exclude = "seller")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellerMarketplace extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false, unique = true)
    @NotNull
    @JsonIgnore
    private Tenant seller;

    @NotBlank
    @Column(name = "storefront_name", nullable = false)
    private String storefrontName;

    @NotBlank
    @Column(name = "storefront_slug", nullable = false, unique = true)
    private String storefrontSlug;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // Feature Overrides (can override global settings)
    @Column(name = "allow_direct_purchase")
    private Boolean allowDirectPurchase;

    @Column(name = "require_quote_approval")
    private Boolean requireQuoteApproval;

    @Column(name = "min_order_value", precision = 10, scale = 2)
    private BigDecimal minOrderValue;

    // Seller Preferences
    @Column(name = "auto_accept_orders")
    private Boolean autoAcceptOrders = false;

    @Column(name = "allow_negotiations")
    private Boolean allowNegotiations = true;
}
