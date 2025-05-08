package com.quorion.b2b.model.commerce;

import com.quorion.b2b.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Delivery terms (FOB, CIF, etc.)
 */
@Entity
@Table(name = "delivery_term")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryTerm extends BaseEntity {

    @NotBlank
    @Column(name = "code", nullable = false, unique = true, length = 20)
    private String code;

    @NotBlank
    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}
