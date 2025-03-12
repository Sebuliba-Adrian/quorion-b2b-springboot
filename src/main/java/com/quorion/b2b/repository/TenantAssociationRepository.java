package com.quorion.b2b.repository;

import com.quorion.b2b.model.tenant.Tenant;
import com.quorion.b2b.model.tenant.TenantAssociation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for TenantAssociation entity
 */
@Repository
public interface TenantAssociationRepository extends JpaRepository<TenantAssociation, UUID> {

    List<TenantAssociation> findBySellerAndIsActiveTrue(Tenant seller);

    List<TenantAssociation> findByBuyerAndIsActiveTrue(Tenant buyer);

    List<TenantAssociation> findBySellerId(UUID sellerId);

    List<TenantAssociation> findByBuyerId(UUID buyerId);

    List<TenantAssociation> findByIsActive(Boolean isActive);
}
