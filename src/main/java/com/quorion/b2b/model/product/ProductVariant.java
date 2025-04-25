package com.quorion.b2b.model.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.quorion.b2b.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

/**
 * Product variant (combination of attributes)
 */
@Entity
@Table(name = "product_variant")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"product", "sku"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariant extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @NotNull
    @JsonIgnore
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sku_id", nullable = false)
    @NotNull
    @JsonIgnore
    private ProductSKU sku;

    @NotBlank
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "price_adjustment", precision = 10, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal priceAdjustment = BigDecimal.ZERO;

    @Column(name = "stock_quantity", nullable = false)
    @Min(0)
    @Builder.Default
    private Integer stockQuantity = 0;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
}
