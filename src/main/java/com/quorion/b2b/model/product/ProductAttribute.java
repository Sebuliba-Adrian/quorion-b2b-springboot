package com.quorion.b2b.model.product;

import com.quorion.b2b.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Product attribute definition (e.g., Color, Size, Material)
 */
@Entity
@Table(name = "product_attribute")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductAttribute extends BaseEntity {

    @NotBlank
    @Column(name = "name", unique = true, nullable = false, length = 100)
    private String name;

    @NotBlank
    @Column(name = "slug", unique = true, nullable = false, length = 100)
    private String slug;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}
