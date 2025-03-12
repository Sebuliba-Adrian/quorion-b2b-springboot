package com.quorion.b2b.repository;

import com.quorion.b2b.model.tenant.Tenant;
import com.quorion.b2b.model.tenant.TenantType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Tenant entity
 */
@Repository
public interface TenantRepository extends JpaRepository<Tenant, UUID> {

    List<Tenant> findByType(TenantType type);

    List<Tenant> findByIsActiveTrue();

    Optional<Tenant> findByEmail(String email);

    @Query("SELECT t FROM Tenant t WHERE t.type = :type AND t.isActive = true")
    List<Tenant> findActiveByType(TenantType type);

    @Query("SELECT DISTINCT t FROM Tenant t " +
           "LEFT JOIN FETCH t.addresses " +
           "WHERE t.id = :id")
    Optional<Tenant> findByIdWithAddresses(UUID id);
}
