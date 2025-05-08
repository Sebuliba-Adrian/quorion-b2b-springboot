package com.quorion.b2b.model.commerce;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.quorion.b2b.model.BaseEntity;
import com.quorion.b2b.model.tenant.Tenant;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Sales lead from buyer with state machine
 */
@Entity
@Table(name = "lead")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"seller", "cart", "customer", "parentLead", "childLeads"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lead extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    @NotNull
    @JsonIgnore
    private Tenant seller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    @JsonIgnore
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    @JsonIgnore
    private Customer customer;

    @NotBlank
    @Column(name = "buyer_first_name", nullable = false, length = 100)
    private String buyerFirstName;

    @NotBlank
    @Column(name = "buyer_last_name", nullable = false, length = 100)
    private String buyerLastName;

    @Email
    @NotBlank
    @Column(name = "buyer_email", nullable = false)
    private String buyerEmail;

    @Column(name = "buyer_phone", length = 50)
    private String buyerPhone;

    @Column(name = "buyer_company_name")
    private String buyerCompanyName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private SalesLeadStatus status = SalesLeadStatus.NO_LEAD;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_lead_id")
    @JsonIgnore
    private Lead parentLead;

    @OneToMany(mappedBy = "parentLead", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Lead> childLeads = new HashSet<>();

    @Column(name = "source", length = 50)
    private String source = "web";
}
