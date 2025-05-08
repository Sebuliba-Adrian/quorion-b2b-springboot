package com.quorion.b2b.model.commerce;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.quorion.b2b.model.BaseEntity;
import com.quorion.b2b.model.tenant.Tenant;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

/**
 * Customer created from converted lead
 */
@Entity
@Table(name = "customer", indexes = {
    @Index(name = "idx_customer_email", columnList = "email"),
    @Index(name = "idx_customer_tenant_email", columnList = "tenant_id,email")
})
@Data
@EqualsAndHashCode(callSuper = true, exclude = "tenant")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    @NotNull
    @JsonIgnore
    private Tenant tenant;

    @NotBlank
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @NotBlank
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Email
    @NotBlank
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "phone", length = 50)
    private String phone;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "tax_id", length = 100)
    private String taxId;

    @Column(name = "credit_limit", precision = 12, scale = 2)
    private BigDecimal creditLimit = BigDecimal.ZERO;

    @Column(name = "payment_terms_days")
    private Integer paymentTermsDays = 30;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    public String getFullName() {
        return firstName + " " + lastName;
    }
}
