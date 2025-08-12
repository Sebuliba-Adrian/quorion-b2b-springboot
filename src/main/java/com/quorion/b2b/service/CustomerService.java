package com.quorion.b2b.service;

import com.quorion.b2b.model.commerce.Customer;
import com.quorion.b2b.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository repository;

    @Transactional(readOnly = true)
    public List<Customer> getAll(UUID tenantId, String email, Boolean isActive) {
        if (tenantId != null) {
            return repository.findByTenantId(tenantId);
        } else if (email != null) {
            return repository.findByEmail(email).map(List::of).orElse(List.of());
        } else if (isActive != null) {
            return repository.findByIsActive(isActive);
        }
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Customer getById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Customer not found"));
    }

    @Transactional
    public Customer create(Customer entity) {
        return repository.save(entity);
    }

    @Transactional
    public Customer update(UUID id, Customer details) {
        Customer entity = getById(id);
        if (details.getFirstName() != null) entity.setFirstName(details.getFirstName());
        if (details.getLastName() != null) entity.setLastName(details.getLastName());
        if (details.getEmail() != null) entity.setEmail(details.getEmail());
        if (details.getPhone() != null) entity.setPhone(details.getPhone());
        if (details.getCompanyName() != null) entity.setCompanyName(details.getCompanyName());
        if (details.getIsActive() != null) entity.setIsActive(details.getIsActive());
        return repository.save(entity);
    }

    @Transactional
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new jakarta.persistence.EntityNotFoundException("Customer not found with id: " + id);
        }
        repository.deleteById(id);
    }
}
