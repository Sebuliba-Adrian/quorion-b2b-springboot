package com.quorion.b2b.model.commerce;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.quorion.b2b.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Payment transactions
 */
@Entity
@Table(name = "payment")
@Data
@EqualsAndHashCode(callSuper = true, exclude = "order")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @NotNull
    @JsonIgnore
    private PurchaseOrder order;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 50)
    private PaymentMethod paymentMethod;

    @NotNull
    @DecimalMin(value = "0.01")
    @Column(name = "amount", precision = 12, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "currency", nullable = false, length = 3)
    @Builder.Default
    private String currency = "USD";

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(name = "payment_gateway", length = 50)
    private String paymentGateway;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "gateway_response", columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, Object> gatewayResponse = new HashMap<>();

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;
}
