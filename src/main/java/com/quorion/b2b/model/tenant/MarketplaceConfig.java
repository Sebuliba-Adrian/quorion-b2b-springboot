package com.quorion.b2b.model.tenant;

import com.quorion.b2b.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

/**
 * Global marketplace configuration with feature flags
 * Singleton pattern - only one active config at a time
 */
@Entity
@Table(name = "marketplace_config")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MarketplaceConfig extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name = "Main Marketplace";

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @NotNull(message = "Marketplace mode is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "mode", nullable = false, length = 50)
    private MarketplaceMode mode;

    // Feature Flags - Cart & Shopping
    @Column(name = "enable_shopping_cart")
    private Boolean enableShoppingCart = true;

    @Column(name = "enable_guest_checkout")
    private Boolean enableGuestCheckout = false;

    @Column(name = "enable_wishlist")
    private Boolean enableWishlist = false;

    // Feature Flags - B2B Specific
    @Column(name = "enable_lead_generation")
    private Boolean enableLeadGeneration = true;

    @Column(name = "enable_quote_negotiation")
    private Boolean enableQuoteNegotiation = true;

    @Column(name = "require_quote_approval")
    private Boolean requireQuoteApproval = true;

    @Column(name = "enable_distributor_network")
    private Boolean enableDistributorNetwork = true;

    // Feature Flags - Direct Purchase
    @Column(name = "enable_direct_purchase")
    private Boolean enableDirectPurchase = false;

    @Column(name = "enable_instant_checkout")
    private Boolean enableInstantCheckout = false;

    @Column(name = "skip_quote_for_direct")
    private Boolean skipQuoteForDirect = false;

    // Feature Flags - Multi-Vendor
    @Column(name = "enable_multiple_sellers")
    private Boolean enableMultipleSellers = true;

    @Column(name = "enable_seller_storefronts")
    private Boolean enableSellerStorefronts = false;

    @Column(name = "allow_cross_seller_cart")
    private Boolean allowCrossSellerCart = false;

    // Feature Flags - Pricing
    @Column(name = "enable_dynamic_pricing")
    private Boolean enableDynamicPricing = true;

    @Column(name = "enable_volume_discounts")
    private Boolean enableVolumeDiscounts = true;

    @Column(name = "show_prices_to_guests")
    private Boolean showPricesToGuests = false;

    @Column(name = "enable_promotional_pricing")
    private Boolean enablePromotionalPricing = false;

    // Feature Flags - Customer Management
    @Column(name = "enable_customer_accounts")
    private Boolean enableCustomerAccounts = true;

    @Column(name = "enable_customer_portal")
    private Boolean enableCustomerPortal = false;

    @Column(name = "enable_saved_addresses")
    private Boolean enableSavedAddresses = true;

    // Feature Flags - Payment
    @Column(name = "enable_online_payment")
    private Boolean enableOnlinePayment = false;

    @Column(name = "enable_credit_terms")
    private Boolean enableCreditTerms = true;

    @Column(name = "enable_partial_payments")
    private Boolean enablePartialPayments = false;

    // Feature Flags - Reviews & Ratings
    @Column(name = "enable_product_reviews")
    private Boolean enableProductReviews = false;

    @Column(name = "enable_seller_ratings")
    private Boolean enableSellerRatings = false;

    @Column(name = "require_verified_purchase")
    private Boolean requireVerifiedPurchase = true;

    // Workflow Settings
    @Column(name = "auto_approve_quotes")
    private Boolean autoApproveQuotes = false;

    @Column(name = "auto_create_customer")
    private Boolean autoCreateCustomer = true;

    @Column(name = "send_notifications")
    private Boolean sendNotifications = true;

    // Limits & Restrictions
    @Column(name = "min_order_value", precision = 10, scale = 2)
    private BigDecimal minOrderValue = BigDecimal.ZERO;

    @Column(name = "max_cart_items")
    private Integer maxCartItems = 100;

    @Column(name = "session_timeout_minutes")
    private Integer sessionTimeoutMinutes = 30;

    public boolean isB2bMode() {
        return mode == MarketplaceMode.B2B_NEGOTIATION || mode == MarketplaceMode.HYBRID;
    }

    public boolean isMarketplaceMode() {
        return mode == MarketplaceMode.DIRECT_MARKETPLACE ||
               mode == MarketplaceMode.MULTI_VENDOR ||
               mode == MarketplaceMode.HYBRID;
    }

    // Alias methods for compatibility
    public Boolean getAllowDirectPurchase() {
        return enableDirectPurchase;
    }

    public void setAllowDirectPurchase(Boolean allowDirectPurchase) {
        this.enableDirectPurchase = allowDirectPurchase;
    }

    public Boolean getAllowGuestCheckout() {
        return enableGuestCheckout;
    }

    public void setAllowGuestCheckout(Boolean allowGuestCheckout) {
        this.enableGuestCheckout = allowGuestCheckout;
    }
}
