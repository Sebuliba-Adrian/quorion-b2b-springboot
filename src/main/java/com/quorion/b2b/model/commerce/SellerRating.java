package com.quorion.b2b.model.commerce;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.quorion.b2b.model.BaseEntity;
import com.quorion.b2b.model.tenant.Tenant;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Seller rating from buyers
 */
@Entity
@Table(name = "seller_rating", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"seller_id", "buyer_id", "order_id"})
})
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"seller", "buyer", "order"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellerRating extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    @NotNull
    @JsonIgnore
    private Tenant seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    @NotNull
    @JsonIgnore
    private Tenant buyer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    @JsonIgnore
    private PurchaseOrder order;

    @NotNull
    @Min(1)
    @Max(5)
    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Min(1)
    @Max(5)
    @Column(name = "communication_rating")
    private Integer communicationRating;

    @Min(1)
    @Max(5)
    @Column(name = "shipping_speed_rating")
    private Integer shippingSpeedRating;

    @Min(1)
    @Max(5)
    @Column(name = "product_quality_rating")
    private Integer productQualityRating;

    @Column(name = "review", columnDefinition = "TEXT")
    private String review;

    @Column(name = "is_approved", nullable = false)
    @Builder.Default
    private Boolean isApproved = false;
}
