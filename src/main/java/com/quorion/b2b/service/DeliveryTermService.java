package com.quorion.b2b.service;

import com.quorion.b2b.model.commerce.DeliveryTerm;
import com.quorion.b2b.repository.DeliveryTermRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryTermService {
    private final DeliveryTermRepository repository;

    @Transactional(readOnly = true)
    public List<DeliveryTerm> getAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public DeliveryTerm getById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("DeliveryTerm not found"));
    }

    @Transactional
    public DeliveryTerm create(DeliveryTerm entity) {
        return repository.save(entity);
    }

    @Transactional
    public DeliveryTerm update(UUID id, DeliveryTerm details) {
        DeliveryTerm entity = getById(id);
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
