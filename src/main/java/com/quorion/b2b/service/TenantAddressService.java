package com.quorion.b2b.service;

import com.quorion.b2b.model.tenant.TenantAddress;
import com.quorion.b2b.model.tenant.AddressType;
import com.quorion.b2b.repository.TenantAddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * TenantAddress Service
 */
@Service
@RequiredArgsConstructor
public class TenantAddressService {

    private final TenantAddressRepository tenantAddressRepository;

    @Transactional(readOnly = true)
    public List<TenantAddress> getAllAddresses(UUID tenantId, AddressType addressType, Boolean isActive) {
        if (tenantId != null && addressType != null && isActive != null) {
            return tenantAddressRepository.findByTenantIdAndAddressTypeAndIsActive(tenantId, addressType, isActive);
        } else if (tenantId != null && addressType != null) {
            return tenantAddressRepository.findByTenantIdAndAddressType(tenantId, addressType);
        } else if (tenantId != null && isActive != null) {
            return tenantAddressRepository.findByTenantIdAndIsActive(tenantId, isActive);
        } else if (tenantId != null) {
            return tenantAddressRepository.findByTenantId(tenantId);
        }
        return tenantAddressRepository.findAll();
    }

    @Transactional(readOnly = true)
    public TenantAddress getAddressById(UUID id) {
        return tenantAddressRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Address not found"));
    }

    @Transactional
    public TenantAddress createAddress(TenantAddress address) {
        return tenantAddressRepository.save(address);
    }

    @Transactional
    public TenantAddress updateAddress(UUID id, TenantAddress addressDetails) {
        TenantAddress address = getAddressById(id);
        // Update fields
        if (addressDetails.getAddressType() != null) address.setAddressType(addressDetails.getAddressType());
        if (addressDetails.getStreet1() != null) address.setStreet1(addressDetails.getStreet1());
        if (addressDetails.getStreet2() != null) address.setStreet2(addressDetails.getStreet2());
        if (addressDetails.getCity() != null) address.setCity(addressDetails.getCity());
        if (addressDetails.getState() != null) address.setState(addressDetails.getState());
        if (addressDetails.getCountry() != null) address.setCountry(addressDetails.getCountry());
        if (addressDetails.getPostalCode() != null) address.setPostalCode(addressDetails.getPostalCode());
        if (addressDetails.getIsActive() != null) address.setIsActive(addressDetails.getIsActive());
        return tenantAddressRepository.save(address);
    }

    @Transactional
    public void deleteAddress(UUID id) {
        if (!tenantAddressRepository.existsById(id)) {
            throw new jakarta.persistence.EntityNotFoundException("Address not found with id: " + id);
        }
        tenantAddressRepository.deleteById(id);
    }
}
