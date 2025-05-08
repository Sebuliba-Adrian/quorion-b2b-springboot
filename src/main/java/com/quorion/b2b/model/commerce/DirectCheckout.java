package com.quorion.b2b.model.commerce;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.quorion.b2b.model.BaseEntity;
import com.quorion.b2b.model.tenant.Tenant;
import com.quorion.b2b.model.tenant.TenantAddress;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Direct checkout session (bypass quote negotiation)
 */
@Entity
@Table(name = "direct_checkout")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"cart", "buyer", "shippingAddress", "billingAddress", "order"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DirectCheckout extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    @NotNull
    @JsonIgnore
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    @NotNull
    @JsonIgnore
    private Tenant buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipping_address_id", nullable = false)
    @NotNull
    private TenantAddress shippingAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_address_id", nullable = false)
    @NotNull
    private TenantAddress billingAddress;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 50)
    private PaymentMethod paymentMethod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_term_id", nullable = false)
    @NotNull
    private DeliveryTerm deliveryTerm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_term_id")
    private PaymentTerm paymentTerm;

    @Column(name = "subtotal", precision = 12, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(name = "tax_amount", precision = 12, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(name = "shipping_cost", precision = 12, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal shippingCost = BigDecimal.ZERO;

    @Column(name = "discount_amount", precision = 12, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "total_amount", precision = 12, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(name = "currency", nullable = false, length = 3)
    @Builder.Default
    private String currency = "USD";

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "is_completed", nullable = false)
    @Builder.Default
    private Boolean isCompleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    @JsonIgnore
    private PurchaseOrder order;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;
}
