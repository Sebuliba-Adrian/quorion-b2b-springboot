package com.quorion.b2b.model.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.quorion.b2b.model.BaseEntity;
import com.quorion.b2b.model.tenant.Tenant;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Base product model
 */
@Entity
@Table(name = "product")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"seller", "skus"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    @NotNull
    @JsonIgnore
    private Tenant seller;

    @NotBlank
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "short_description", length = 500)
    private String shortDescription;

    @Column(name = "slug", unique = true, length = 255)
    private String slug;

    @NotBlank
    @Column(name = "brand_product_name", nullable = false)
    private String brandProductName;

    @Column(name = "category", length = 100)
    private String category;

    @Column(name = "brand", length = 100)
    private String brand;

    @Column(name = "manufacturer", length = 100)
    private String manufacturer;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ProductStatus status = ProductStatus.DRAFT;

    @Column(name = "view_count", nullable = false)
    private Long viewCount = 0L;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<ProductSKU> skus = new HashSet<>();
}
