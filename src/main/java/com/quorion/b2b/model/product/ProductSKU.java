package com.quorion.b2b.model.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.quorion.b2b.model.BaseEntity;
import com.quorion.b2b.model.tenant.Tenant;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

/**
 * Product SKU with packaging information
 */
@Entity
@Table(name = "product_sku")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"product", "distributor", "buyer", "packagingType", "packagingUnit", "originalSku"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSKU extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @NotNull
    @JsonIgnore
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "distributor_id")
    @JsonIgnore
    private Tenant distributor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id")
    @JsonIgnore
    private Tenant buyer;

    @NotBlank
    @Column(name = "number", nullable = false, unique = true, length = 100)
    private String number;

    @Column(name = "name")
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "kind", nullable = false, length = 20)
    private SKUKind kind = SKUKind.PRODUCT_SKU;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_sku_id")
    @JsonIgnore
    private ProductSKU originalSku;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "packaging_type_id", nullable = false)
    @NotNull
    @JsonIgnore
    private PackagingType packagingType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "packaging_unit_id", nullable = false)
    @NotNull
    @JsonIgnore
    private PackagingUnit packagingUnit;

    @NotNull
    @DecimalMin(value = "0.01")
    @Column(name = "package_volume", nullable = false, precision = 10, scale = 2)
    private BigDecimal packageVolume;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    /**
     * Check if SKU is owned or distributed by tenant
     */
    public boolean isOwnedOrDistributedBy(Tenant tenant) {
        if (tenant.equals(product.getSeller())) {
            return true;
        }
        if (distributor != null && tenant.equals(distributor)) {
            return true;
        }
        return false;
    }

    /**
     * Calculate total quantity from number of units
     */
    public BigDecimal getTotalQuantityForUnits(BigDecimal noOfUnits) {
        if (packageVolume != null) {
            return noOfUnits.multiply(packageVolume);
        }
        return null;
    }
}
