package com.quorion.b2b.model.tenant;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.quorion.b2b.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Address for tenant (warehouse, headquarters, etc.)
 */
@Entity
@Table(name = "tenant_address")
@Data
@EqualsAndHashCode(callSuper = true, exclude = "tenant")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TenantAddress extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    @NotNull
    @JsonIgnore
    private Tenant tenant;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "address_type", nullable = false, length = 50)
    private AddressType addressType;

    @NotBlank
    @Column(name = "address1", nullable = false)
    private String address1;

    @Column(name = "address2")
    private String address2;

    @NotBlank
    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @Column(name = "state", length = 100)
    private String state;

    @NotBlank
    @Column(name = "zip_code", nullable = false, length = 20)
    private String zipCode;

    @NotBlank
    @Column(name = "country", nullable = false, length = 100)
    private String country;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // Alias methods for compatibility
    public String getStreet1() {
        return address1;
    }

    public void setStreet1(String street1) {
        this.address1 = street1;
    }

    public String getStreet2() {
        return address2;
    }

    public void setStreet2(String street2) {
        this.address2 = street2;
    }

    public String getPostalCode() {
        return zipCode;
    }

    public void setPostalCode(String postalCode) {
        this.zipCode = postalCode;
    }
}
