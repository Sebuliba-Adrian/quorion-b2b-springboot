package com.quorion.b2b.service;

import com.quorion.b2b.model.commerce.PaymentMode;
import com.quorion.b2b.repository.PaymentModeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentModeService {
    private final PaymentModeRepository repository;

    @Transactional(readOnly = true)
    public List<PaymentMode> getAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public PaymentMode getById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("PaymentMode not found"));
    }

    @Transactional
    public PaymentMode create(PaymentMode entity) {
        return repository.save(entity);
    }

    @Transactional
    public PaymentMode update(UUID id, PaymentMode details) {
        PaymentMode entity = getById(id);
        if (details.getName() != null) entity.setName(details.getName());
        if (details.getCode() != null) entity.setCode(details.getCode());
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
