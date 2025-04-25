package com.quorion.b2b.model.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.quorion.b2b.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Wishlist item
 */
@Entity
@Table(name = "wishlist_item", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"wishlist_id", "product_id", "variant_id"})
})
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"wishlist", "product", "variant"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WishlistItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wishlist_id", nullable = false)
    @NotNull
    @JsonIgnore
    private Wishlist wishlist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @NotNull
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id")
    private ProductVariant variant;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "added_at", nullable = false)
    private LocalDateTime addedAt;

    @PrePersist
    protected void onCreateWishlistItem() {
        if (addedAt == null) {
            addedAt = LocalDateTime.now();
        }
    }
}
