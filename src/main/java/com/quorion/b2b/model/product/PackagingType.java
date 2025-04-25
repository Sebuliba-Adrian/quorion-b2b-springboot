package com.quorion.b2b.model.product;

import com.quorion.b2b.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Packaging type (e.g., Drum, Bag, Bottle)
 */
@Entity
@Table(name = "packaging_type")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PackagingType extends BaseEntity {

    @NotBlank
    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}
