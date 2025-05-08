package com.quorion.b2b.model.commerce;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.quorion.b2b.model.BaseEntity;
import com.quorion.b2b.model.product.ProductSKU;
import com.quorion.b2b.model.tenant.Tenant;
import com.quorion.b2b.model.tenant.TenantAddress;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Volume-based pricing tiers
 */
@Entity
@Table(name = "price_tier")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"seller", "buyer", "destination", "productSku", "deliveryTerm", "paymentTerm"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceTier extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    @NotNull
    @JsonIgnore
    private Tenant seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    @NotNull
    @JsonIgnore
    private Tenant buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_id", nullable = false)
    @NotNull
    @JsonIgnore
    private TenantAddress destination;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_sku_id", nullable = false)
    @NotNull
    @JsonIgnore
    private ProductSKU productSku;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_term_id")
    @JsonIgnore
    private DeliveryTerm deliveryTerm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_term_id")
    @JsonIgnore
    private PaymentTerm paymentTerm;

    @NotNull
    @DecimalMin(value = "0.01")
    @Column(name = "minimum_uom_quantity", nullable = false, precision = 10, scale = 2)
    private BigDecimal minimumUomQuantity;

    @Column(name = "maximum_uom_quantity", precision = 10, scale = 2)
    private BigDecimal maximumUomQuantity;

    @NotNull
    @DecimalMin(value = "0.01")
    @Column(name = "price_per_uom", nullable = false, precision = 10, scale = 2)
    private BigDecimal pricePerUom;

    @Column(name = "discount_percent", precision = 5, scale = 2)
    private BigDecimal discountPercent;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency = "USD";

    @Column(name = "valid_from_date")
    private LocalDateTime validFromDate;

    @Column(name = "valid_to_date")
    private LocalDateTime validToDate;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // Alias methods for compatibility
    public BigDecimal getMinQuantity() {
        return minimumUomQuantity;
    }

    public void setMinQuantity(BigDecimal minQuantity) {
        this.minimumUomQuantity = minQuantity;
    }

    public BigDecimal getMaxQuantity() {
        return maximumUomQuantity;
    }

    public void setMaxQuantity(BigDecimal maxQuantity) {
        this.maximumUomQuantity = maxQuantity;
    }

    public BigDecimal getPrice() {
        return pricePerUom;
    }

    public void setPrice(BigDecimal price) {
        this.pricePerUom = price;
    }

    /**
     * Get best price for given quantity from price tiers
     */
    public static BigDecimal getCurrentPrice(BigDecimal quantity, List<PriceTier> priceTiers) {
        LocalDateTime now = LocalDateTime.now();

        return priceTiers.stream()
            .filter(tier -> tier.getIsActive())
            .filter(tier -> quantity.compareTo(tier.getMinimumUomQuantity()) >= 0)
            .filter(tier -> tier.getValidFromDate() == null || !tier.getValidFromDate().isAfter(now))
            .filter(tier -> tier.getValidToDate() == null || !tier.getValidToDate().isBefore(now))
            .map(PriceTier::getPricePerUom)
            .min(BigDecimal::compareTo)
            .orElse(null);
    }
}
