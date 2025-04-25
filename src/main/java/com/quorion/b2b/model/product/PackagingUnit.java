package com.quorion.b2b.model.product;

import com.quorion.b2b.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Packaging unit (e.g., kg, L, gal)
 */
@Entity
@Table(name = "packaging_unit")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PackagingUnit extends BaseEntity {

    @NotBlank
    @Column(name = "code", nullable = false, unique = true, length = 20)
    private String code;

    @NotBlank
    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}
