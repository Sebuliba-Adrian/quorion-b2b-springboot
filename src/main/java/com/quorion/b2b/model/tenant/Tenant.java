package com.quorion.b2b.model.tenant;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.quorion.b2b.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Multi-tenant company/organization
 */
@Entity
@Table(name = "tenant")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tenant extends BaseEntity {

    @NotBlank
    @Column(name = "name", nullable = false)
    private String name;

    @NotBlank
    @Column(name = "code", nullable = false, unique = true, length = 20)
    private String code;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private TenantType type;

    @Email
    @NotBlank
    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "phone", length = 50)
    private String phone;

    @Column(name = "tax_id", length = 50)
    private String taxId;

    @Column(name = "website", length = 255)
    private String website;

    @Column(name = "contact_phone", length = 50)
    private String contactPhone;

    @Email
    @Column(name = "contact_email", length = 255)
    private String contactEmail;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @JsonIgnore
    @OneToMany(mappedBy = "tenant", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<TenantAddress> addresses = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<TenantAssociation> buyerAssociations = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "buyer", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<TenantAssociation> sellerAssociations = new HashSet<>();

    public boolean isSeller() {
        return type == TenantType.SELLER;
    }

    public boolean isBuyer() {
        return type == TenantType.BUYER;
    }

    public boolean isDistributor() {
        return type == TenantType.DISTRIBUTOR;
    }
}
