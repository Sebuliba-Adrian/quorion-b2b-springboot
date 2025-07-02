package com.quorion.b2b.service;

import com.quorion.b2b.model.tenant.Tenant;
import com.quorion.b2b.model.tenant.TenantAddress;
import com.quorion.b2b.model.tenant.TenantAssociation;
import com.quorion.b2b.model.tenant.TenantType;
import com.quorion.b2b.repository.TenantAssociationRepository;
import com.quorion.b2b.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Tenant Service
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TenantService {

    private final TenantRepository tenantRepository;
    private final TenantAssociationRepository tenantAssociationRepository;

    /**
     * Get all tenants
     */
    @Transactional(readOnly = true)
    public List<Tenant> getAllTenants() {
        return tenantRepository.findAll();
    }

    /**
     * Get tenant by ID
     */
    @Transactional(readOnly = true)
    public Tenant getTenantById(UUID id) {
        return tenantRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Tenant not found with id: " + id));
    }

    /**
     * Create new tenant
     */
    @Transactional
    public Tenant createTenant(Tenant tenant) {
        return tenantRepository.save(tenant);
    }

    /**
     * Update tenant
     */
    @Transactional
    public Tenant updateTenant(UUID id, Tenant tenantDetails) {
        Tenant tenant = getTenantById(id);

        if (tenantDetails.getName() != null) {
            tenant.setName(tenantDetails.getName());
        }
        if (tenantDetails.getType() != null) {
            tenant.setType(tenantDetails.getType());
        }
        if (tenantDetails.getCode() != null) {
            tenant.setCode(tenantDetails.getCode());
        }
        if (tenantDetails.getTaxId() != null) {
            tenant.setTaxId(tenantDetails.getTaxId());
        }
        if (tenantDetails.getWebsite() != null) {
            tenant.setWebsite(tenantDetails.getWebsite());
        }
        if (tenantDetails.getContactPhone() != null) {
            tenant.setContactPhone(tenantDetails.getContactPhone());
        }
        if (tenantDetails.getContactEmail() != null) {
            tenant.setContactEmail(tenantDetails.getContactEmail());
        }
        if (tenantDetails.getIsActive() != null) {
            tenant.setIsActive(tenantDetails.getIsActive());
        }

        return tenantRepository.save(tenant);
    }

    /**
     * Delete tenant
     */
    @Transactional
    public void deleteTenant(UUID id) {
        Tenant tenant = getTenantById(id);
        tenantRepository.delete(tenant);
    }

    /**
     * Get all addresses for a tenant
     */
    @Transactional(readOnly = true)
    public List<TenantAddress> getTenantAddresses(UUID tenantId) {
        Tenant tenant = getTenantById(tenantId);
        return new ArrayList<>(tenant.getAddresses());
    }

    /**
     * Get distributors for a seller tenant
     */
    @Transactional(readOnly = true)
    public List<Tenant> getDistributors(UUID tenantId) {
        Tenant tenant = getTenantById(tenantId);

        if (tenant.getType() != TenantType.SELLER) {
            throw new jakarta.persistence.EntityNotFoundException("Only sellers have distributors");
        }

        List<TenantAssociation> associations = tenantAssociationRepository
                .findBySellerAndIsActiveTrue(tenant);

        return associations.stream()
                .map(TenantAssociation::getBuyer)
                .filter(buyer -> buyer.getType() == TenantType.DISTRIBUTOR)
                .collect(Collectors.toList());
    }
}
