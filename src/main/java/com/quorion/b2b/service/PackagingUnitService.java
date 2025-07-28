package com.quorion.b2b.service;

import com.quorion.b2b.model.product.PackagingUnit;
import com.quorion.b2b.repository.PackagingUnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PackagingUnitService {
    private final PackagingUnitRepository repository;

    @Transactional(readOnly = true)
    public List<PackagingUnit> getAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public PackagingUnit getById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("PackagingUnit not found"));
    }

    @Transactional
    public PackagingUnit create(PackagingUnit entity) {
        return repository.save(entity);
    }

    @Transactional
    public PackagingUnit update(UUID id, PackagingUnit details) {
        PackagingUnit entity = getById(id);
        if (details.getName() != null) entity.setName(details.getName());
        if (details.getCode() != null) entity.setCode(details.getCode());
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
