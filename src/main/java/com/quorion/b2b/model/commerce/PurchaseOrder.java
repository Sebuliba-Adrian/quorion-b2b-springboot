package com.quorion.b2b.model.commerce;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.quorion.b2b.model.BaseEntity;
import com.quorion.b2b.model.tenant.Tenant;
import com.quorion.b2b.model.tenant.TenantAddress;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Purchase order (confirmed order) with state machine
 */
@Entity
@Table(name = "purchase_order")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"buyer", "seller", "quoteRequest", "warehouse", "deliveryTerm", "paymentTerm", "paymentMode", "items", "shipments"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseOrder extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    @NotNull
    @JsonIgnore
    private Tenant buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    @NotNull
    @JsonIgnore
    private Tenant seller;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quote_request_id")
    @JsonIgnore
    private QuoteRequest quoteRequest;

    @NotBlank
    @Column(name = "number", nullable = false, unique = true, length = 50)
    private String number;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private OrderStatus status = OrderStatus.NO_ORDER;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    @NotNull
    @JsonIgnore
    private TenantAddress warehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_term_id", nullable = false)
    @NotNull
    @JsonIgnore
    private DeliveryTerm deliveryTerm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_term_id", nullable = false)
    @NotNull
    @JsonIgnore
    private PaymentTerm paymentTerm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_mode_id", nullable = false)
    @NotNull
    @JsonIgnore
    private PaymentMode paymentMode;

    @NotNull
    @DecimalMin(value = "0.00")
    @Column(name = "shipping_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal shippingCost = BigDecimal.ZERO;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency = "USD";

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PurchaseOrderDetail> items = new ArrayList<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ShipmentAdvice> shipments = new ArrayList<>();

    public BigDecimal getSubtotal() {
        return items.stream()
            .map(PurchaseOrderDetail::getTotalValue)
            .filter(val -> val != null)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotal() {
        return getSubtotal().add(shippingCost);
    }
}
