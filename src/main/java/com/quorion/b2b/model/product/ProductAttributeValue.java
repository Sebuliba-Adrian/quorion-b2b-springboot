package com.quorion.b2b.model.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.quorion.b2b.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Product attribute value (e.g., Red, Blue, Large, Small)
 */
@Entity
@Table(name = "product_attribute_value", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"attribute_id", "attribute_value"})
})
@Data
@EqualsAndHashCode(callSuper = true, exclude = "attribute")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductAttributeValue extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_id", nullable = false)
    @NotNull
    @JsonIgnore
    private ProductAttribute attribute;

    @NotBlank
    @Column(name = "attribute_value", nullable = false)
    private String value;

    @NotBlank
    @Column(name = "slug", nullable = false)
    private String slug;
}
