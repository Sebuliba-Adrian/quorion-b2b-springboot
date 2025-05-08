package com.quorion.b2b.model.commerce;

import com.quorion.b2b.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Promotional discount codes
 */
@Entity
@Table(name = "promo_code")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromoCode extends BaseEntity {

    @NotBlank
    @Column(name = "code", unique = true, nullable = false, length = 50)
    private String code;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotNull
    @Column(name = "discount_type", nullable = false, length = 20)
    @Builder.Default
    private String discountType = "percentage"; // percentage or fixed

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "discount_value", precision = 10, scale = 2, nullable = false)
    private BigDecimal discountValue;

    @Column(name = "min_purchase_amount", precision = 12, scale = 2, nullable = false)
    @DecimalMin(value = "0")
    @Builder.Default
    private BigDecimal minPurchaseAmount = BigDecimal.ZERO;

    @Column(name = "max_discount_amount", precision = 12, scale = 2)
    private BigDecimal maxDiscountAmount;

    @Column(name = "usage_limit")
    private Integer usageLimit;

    @Column(name = "usage_count", nullable = false)
    @Builder.Default
    private Integer usageCount = 0;

    @Column(name = "per_user_limit")
    private Integer perUserLimit;

    @NotNull
    @Column(name = "valid_from", nullable = false)
    private LocalDateTime validFrom;

    @NotNull
    @Column(name = "valid_until", nullable = false)
    private LocalDateTime validUntil;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    /**
     * Check if promo code is currently valid
     */
    public Boolean isValid() {
        LocalDateTime now = LocalDateTime.now();
        if (!isActive) return false;
        if (now.isBefore(validFrom) || now.isAfter(validUntil)) return false;
        if (usageLimit != null && usageCount >= usageLimit) return false;
        return true;
    }

    /**
     * Calculate discount amount for given purchase amount
     */
    public BigDecimal calculateDiscount(BigDecimal amount) {
        if (!isValid()) return BigDecimal.ZERO;
        if (amount.compareTo(minPurchaseAmount) < 0) return BigDecimal.ZERO;

        if ("percentage".equals(discountType)) {
            BigDecimal discount = amount.multiply(discountValue.divide(BigDecimal.valueOf(100)));
            if (maxDiscountAmount != null && discount.compareTo(maxDiscountAmount) > 0) {
                return maxDiscountAmount;
            }
            return discount;
        } else { // fixed
            return discountValue.min(amount);
        }
    }
}
