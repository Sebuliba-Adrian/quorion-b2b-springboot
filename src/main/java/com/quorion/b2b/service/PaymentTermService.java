package com.quorion.b2b.service;

import com.quorion.b2b.model.commerce.PaymentTerm;
import com.quorion.b2b.repository.PaymentTermRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentTermService {
    private final PaymentTermRepository repository;

    @Transactional(readOnly = true)
    public List<PaymentTerm> getAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public PaymentTerm getById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("PaymentTerm not found"));
    }

    @Transactional
    public PaymentTerm create(PaymentTerm entity) {
        return repository.save(entity);
    }

    @Transactional
    public PaymentTerm update(UUID id, PaymentTerm details) {
        PaymentTerm entity = getById(id);
        if (details.getName() != null) entity.setName(details.getName());
        if (details.getCode() != null) entity.setCode(details.getCode());
        if (details.getDaysUntilDue() != null) entity.setDaysUntilDue(details.getDaysUntilDue());
        if (details.getDescription() != null) entity.setDescription(details.getDescription());
        return repository.save(entity);
    }

    @Transactional
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new jakarta.persistence.EntityNotFoundException("Entity not found with id: " + id);
        }
        repository.deleteById(id);
    }
}
