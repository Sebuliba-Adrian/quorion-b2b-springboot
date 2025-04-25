package com.quorion.b2b.model.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.quorion.b2b.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Base list price for SKU
 */
@Entity
@Table(name = "list_price")
@Data
@EqualsAndHashCode(callSuper = true, exclude = "sku")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListPrice extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sku_id", nullable = false)
    @NotNull
    @JsonIgnore
    private ProductSKU sku;

    @NotNull
    @DecimalMin(value = "0.01")
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency = "USD";

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // Alias method for compatibility
    public LocalDateTime getEffectiveDate() {
        return startDate;
    }

    public void setEffectiveDate(LocalDateTime effectiveDate) {
        this.startDate = effectiveDate;
    }
}
