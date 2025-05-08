package com.quorion.b2b.model.commerce;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.quorion.b2b.model.BaseEntity;
import com.quorion.b2b.model.product.Product;
import com.quorion.b2b.model.tenant.Tenant;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Track product views for analytics
 */
@Entity
@Table(name = "product_view")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"product", "viewer"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductView extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @NotNull
    @JsonIgnore
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "viewer_id")
    @JsonIgnore
    private Tenant viewer;

    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "referrer", length = 500)
    private String referrer;

    @Column(name = "viewed_at", nullable = false)
    private LocalDateTime viewedAt;

    @PrePersist
    protected void onCreateProductView() {
        if (viewedAt == null) {
            viewedAt = LocalDateTime.now();
        }
    }
}
