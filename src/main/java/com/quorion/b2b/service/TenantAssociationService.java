package com.quorion.b2b.service;

import com.quorion.b2b.model.tenant.TenantAssociation;
import com.quorion.b2b.repository.TenantAssociationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * TenantAssociation Service
 */
@Service
@RequiredArgsConstructor
public class TenantAssociationService {

    private final TenantAssociationRepository tenantAssociationRepository;

    @Transactional(readOnly = true)
    public List<TenantAssociation> getAllAssociations(UUID sellerId, UUID buyerId, Boolean isActive) {
        if (sellerId != null && buyerId != null) {
            // Filter by both
            return tenantAssociationRepository.findAll().stream()
                    .filter(a -> a.getSeller().getId().equals(sellerId) && a.getBuyer().getId().equals(buyerId))
                    .filter(a -> isActive == null || a.getIsActive().equals(isActive))
                    .toList();
        } else if (sellerId != null) {
            return tenantAssociationRepository.findBySellerId(sellerId);
        } else if (buyerId != null) {
            return tenantAssociationRepository.findByBuyerId(buyerId);
        } else if (isActive != null) {
            return tenantAssociationRepository.findByIsActive(isActive);
        }
        return tenantAssociationRepository.findAll();
    }

    @Transactional(readOnly = true)
    public TenantAssociation getAssociationById(UUID id) {
        return tenantAssociationRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Association not found"));
    }

    @Transactional
    public TenantAssociation createAssociation(TenantAssociation association) {
        return tenantAssociationRepository.save(association);
    }

    @Transactional
    public TenantAssociation updateAssociation(UUID id, TenantAssociation details) {
        TenantAssociation association = getAssociationById(id);
        if (details.getIsActive() != null) association.setIsActive(details.getIsActive());
        return tenantAssociationRepository.save(association);
    }

    @Transactional
    public void deleteAssociation(UUID id) {
        if (!tenantAssociationRepository.existsById(id)) {
            throw new jakarta.persistence.EntityNotFoundException("TenantAssociation not found with id: " + id);
        }
        tenantAssociationRepository.deleteById(id);
    }
}
