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
 * Quote request for price negotiation with state machine
 */
@Entity
@Table(name = "quote_request")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"buyer", "seller", "lead", "warehouse", "deliveryTerm", "paymentTerm", "paymentMode", "items", "purchaseOrder"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuoteRequest extends BaseEntity {

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_id")
    @JsonIgnore
    private Lead lead;

    @NotBlank
    @Column(name = "number", nullable = false, unique = true, length = 50)
    private String number;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private QuoteStatus status = QuoteStatus.NO_REQUEST;

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

    @OneToMany(mappedBy = "quoteRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<QuoteRequestDetail> items = new ArrayList<>();

    @OneToOne(mappedBy = "quoteRequest", cascade = CascadeType.ALL)
    private PurchaseOrder purchaseOrder;

    public BigDecimal getSubtotal() {
        return items.stream()
            .map(QuoteRequestDetail::getTotalValue)
            .filter(val -> val != null)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotal() {
        return getSubtotal().add(shippingCost);
    }
}
