package com.quorion.b2b.model.tenant;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.quorion.b2b.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

/**
 * Association between sellers and buyers (through distributors)
 */
@Entity
@Table(name = "tenant_association", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"seller_id", "buyer_id", "storefront_id"})
})
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"seller", "buyer"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TenantAssociation extends BaseEntity {

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

    @Column(name = "storefront_id")
    private UUID storefrontId;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}
