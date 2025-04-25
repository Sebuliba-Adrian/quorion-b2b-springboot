package com.quorion.b2b.model.product;

import com.quorion.b2b.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Product tags for better organization and search
 */
@Entity
@Table(name = "product_tag")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductTag extends BaseEntity {

    @NotBlank
    @Column(name = "name", unique = true, nullable = false, length = 100)
    private String name;

    @NotBlank
    @Column(name = "slug", unique = true, nullable = false, length = 100)
    private String slug;
}
