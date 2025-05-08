package com.quorion.b2b.model.commerce;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.quorion.b2b.model.BaseEntity;
import com.quorion.b2b.model.product.Product;
import com.quorion.b2b.model.product.ProductSKU;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

/**
 * Line items in quote request
 */
@Entity
@Table(name = "quote_request_detail")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"quoteRequest", "product", "sku", "requestedSku"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuoteRequestDetail extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_request_id", nullable = false)
    @NotNull
    @JsonIgnore
    private QuoteRequest quoteRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @NotNull
    @JsonIgnore
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sku_id")
    @JsonIgnore
    private ProductSKU sku;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_sku_id")
    @JsonIgnore
    private ProductSKU requestedSku;

    @NotNull
    @DecimalMin(value = "0.01")
    @Column(name = "no_of_units", nullable = false, precision = 10, scale = 2)
    private BigDecimal noOfUnits;

    @NotNull
    @DecimalMin(value = "0.01")
    @Column(name = "total_quantity", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalQuantity;

    @DecimalMin(value = "0.01")
    @Column(name = "price_per_unit", precision = 10, scale = 2)
    private BigDecimal pricePerUnit;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency = "USD";

    public BigDecimal getTotalValue() {
        if (pricePerUnit != null) {
            return totalQuantity.multiply(pricePerUnit);
        }
        return BigDecimal.ZERO;
    }
}
