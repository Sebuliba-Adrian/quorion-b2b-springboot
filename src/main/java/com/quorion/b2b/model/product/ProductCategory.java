package com.quorion.b2b.model.product;

import com.quorion.b2b.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Product category with hierarchical structure
 */
@Entity
@Table(name = "product_category")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"parent", "children"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductCategory extends BaseEntity {

    @NotBlank
    @Column(name = "name", nullable = false)
    private String name;

    @NotBlank
    @Column(name = "slug", unique = true, nullable = false)
    private String slug;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private ProductCategory parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @Builder.Default
    private List<ProductCategory> children = new ArrayList<>();

    @Column(name = "image")
    private String image;

    @Column(name = "icon", length = 50)
    private String icon;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "display_order", nullable = false)
    @Builder.Default
    private Integer order = 0;

    /**
     * Get all ancestor categories
     */
    public List<ProductCategory> getAncestors() {
        List<ProductCategory> ancestors = new ArrayList<>();
        ProductCategory current = this.parent;
        while (current != null) {
            ancestors.add(0, current);
            current = current.getParent();
        }
        return ancestors;
    }

    /**
     * Get all descendant categories (recursive)
     */
    public List<ProductCategory> getDescendants() {
        List<ProductCategory> descendants = new ArrayList<>();
        for (ProductCategory child : children) {
            descendants.add(child);
            descendants.addAll(child.getDescendants());
        }
        return descendants;
    }

    /**
     * Get breadcrumb path
     */
    public List<ProductCategory> getBreadcrumb() {
        List<ProductCategory> breadcrumb = getAncestors();
        breadcrumb.add(this);
        return breadcrumb;
    }

    /**
     * Get category level (0 for root, 1 for child, etc.)
     */
    public int getLevel() {
        int level = 0;
        ProductCategory current = this.parent;
        while (current != null) {
            level++;
            current = current.getParent();
        }
        return level;
    }
}
