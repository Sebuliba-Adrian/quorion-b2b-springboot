package com.quorion.b2b.model.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.quorion.b2b.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Link between variant and attribute values
 */
@Entity
@Table(name = "product_variant_attribute", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"variant_id", "attribute_value_id"})
})
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"variant", "attributeValue"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductVariantAttribute extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", nullable = false)
    @NotNull
    @JsonIgnore
    private ProductVariant variant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_value_id", nullable = false)
    @NotNull
    private ProductAttributeValue attributeValue;
}
