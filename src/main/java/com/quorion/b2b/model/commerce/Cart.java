package com.quorion.b2b.model.commerce;

import jakarta.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.quorion.b2b.model.BaseEntity;
import com.quorion.b2b.model.tenant.Tenant;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Enhanced shopping cart with session support
 */
@Entity
@Table(name = "cart", indexes = {
    @Index(name = "idx_cart_session", columnList = "session_key,is_active"),
    @Index(name = "idx_cart_buyer", columnList = "buyer_id,is_active")
})
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"buyer", "customer", "items"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id")
    @JsonIgnore
    private Tenant buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    @JsonIgnore
    private Customer customer;

    @Column(name = "session_key")
    private String sessionKey;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "name")
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CartItem> items = new ArrayList<>();

    public boolean isAnonymous() {
        return buyer == null && customer == null;
    }

    public boolean isExpired() {
        if (expiresAt != null) {
            return LocalDateTime.now().isAfter(expiresAt);
        }
        return false;
    }

    public List<CartItem> getActiveItems() {
        return items.stream()
            .filter(item -> item.getDeletedAt() == null)
            .collect(Collectors.toList());
    }

    public int getTotalItems() {
        return getActiveItems().size();
    }

    public BigDecimal getTotalQuantity() {
        return getActiveItems().stream()
            .map(CartItem::getQuantity)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getSubtotal() {
        return getActiveItems().stream()
            .map(CartItem::getTotalPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void clear() {
        getActiveItems().forEach(CartItem::softDelete);
    }
}
