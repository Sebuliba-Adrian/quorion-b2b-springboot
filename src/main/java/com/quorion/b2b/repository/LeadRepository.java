package com.quorion.b2b.repository;

import com.quorion.b2b.model.commerce.Lead;
import com.quorion.b2b.model.commerce.SalesLeadStatus;
import com.quorion.b2b.model.tenant.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for Lead entity
 */
@Repository
public interface LeadRepository extends JpaRepository<Lead, UUID> {

    List<Lead> findBySeller(Tenant seller);

    List<Lead> findBySellerAndStatus(Tenant seller, SalesLeadStatus status);

    List<Lead> findByParentLead(Lead parentLead);

    List<Lead> findByBuyerEmail(String email);
}
