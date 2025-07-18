package com.quorion.b2b.service;

import com.quorion.b2b.model.commerce.PriceTier;
import com.quorion.b2b.repository.PriceTierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PriceTierService {
    private final PriceTierRepository repository;

    @Transactional(readOnly = true)
    public List<PriceTier> getAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public PriceTier getById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("PriceTier not found"));
    }

    @Transactional
    public PriceTier create(PriceTier entity) {
        return repository.save(entity);
    }

    @Transactional
    public PriceTier update(UUID id, PriceTier details) {
        PriceTier entity = getById(id);
        if (details.getMinQuantity() != null) entity.setMinQuantity(details.getMinQuantity());
        if (details.getMaxQuantity() != null) entity.setMaxQuantity(details.getMaxQuantity());
        if (details.getPrice() != null) entity.setPrice(details.getPrice());
        if (details.getDiscountPercent() != null) entity.setDiscountPercent(details.getDiscountPercent());
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
