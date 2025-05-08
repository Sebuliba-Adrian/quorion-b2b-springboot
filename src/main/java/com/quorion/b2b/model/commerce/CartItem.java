package com.quorion.b2b.model.commerce;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.quorion.b2b.model.BaseEntity;
import com.quorion.b2b.model.product.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Item in shopping cart
 */
@Entity
@Table(name = "cart_item", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"cart_id", "product_id"})
})
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"cart", "product"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    @NotNull
    @JsonIgnore
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @NotNull
    @JsonIgnore
    private Product product;

    @NotNull
    @DecimalMin(value = "0.01")
    @Column(name = "quantity", nullable = false, precision = 10, scale = 2)
    private BigDecimal quantity;

    @NotNull
    @DecimalMin(value = "0.00")
    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public BigDecimal getTotalPrice() {
        return quantity.multiply(unitPrice);
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }
}
