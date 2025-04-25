package com.quorion.b2b.model.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.quorion.b2b.model.BaseEntity;
import com.quorion.b2b.model.tenant.TenantAddress;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Product inventory tracking
 */
@Entity
@Table(name = "inventory", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"product_id", "sku_id", "variant_id", "warehouse_id"})
})
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"product", "sku", "variant", "warehouse"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory extends BaseEntity {

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id")
    @JsonIgnore
    private ProductVariant variant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    @NotNull
    private TenantAddress warehouse;

    @Column(name = "quantity_available", nullable = false)
    @Min(0)
    @Builder.Default
    private Integer quantityAvailable = 0;

    @Column(name = "quantity_reserved", nullable = false)
    @Min(0)
    @Builder.Default
    private Integer quantityReserved = 0;

    @Column(name = "quantity_incoming", nullable = false)
    @Min(0)
    @Builder.Default
    private Integer quantityIncoming = 0;

    @Column(name = "reorder_level", nullable = false)
    @Min(0)
    @Builder.Default
    private Integer reorderLevel = 0;

    @Column(name = "reorder_quantity", nullable = false)
    @Min(0)
    @Builder.Default
    private Integer reorderQuantity = 0;

    @Column(name = "last_restocked_at")
    private LocalDateTime lastRestockedAt;

    /**
     * Total quantity including reserved and incoming
     */
    public Integer getQuantityTotal() {
        return quantityAvailable + quantityReserved + quantityIncoming;
    }

    /**
     * Check if inventory needs reordering
     */
    public Boolean needsReorder() {
        return quantityAvailable <= reorderLevel;
    }
}
