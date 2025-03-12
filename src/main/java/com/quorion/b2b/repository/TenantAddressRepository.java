package com.quorion.b2b.repository;

import com.quorion.b2b.model.tenant.TenantAddress;
import com.quorion.b2b.model.tenant.AddressType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface TenantAddressRepository extends JpaRepository<TenantAddress, UUID> {
    List<TenantAddress> findByTenantId(UUID tenantId);
    List<TenantAddress> findByTenantIdAndAddressType(UUID tenantId, AddressType addressType);
    List<TenantAddress> findByTenantIdAndIsActive(UUID tenantId, Boolean isActive);
    List<TenantAddress> findByTenantIdAndAddressTypeAndIsActive(UUID tenantId, AddressType addressType, Boolean isActive);
}
