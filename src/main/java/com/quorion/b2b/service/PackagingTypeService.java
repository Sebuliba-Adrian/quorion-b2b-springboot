package com.quorion.b2b.service;

import com.quorion.b2b.model.product.PackagingType;
import com.quorion.b2b.repository.PackagingTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PackagingTypeService {
    private final PackagingTypeRepository repository;

    @Transactional(readOnly = true)
    public List<PackagingType> getAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public PackagingType getById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("PackagingType not found"));
    }

    @Transactional
    public PackagingType create(PackagingType entity) {
        return repository.save(entity);
    }

    @Transactional
    public PackagingType update(UUID id, PackagingType details) {
        PackagingType entity = getById(id);
        if (details.getName() != null) entity.setName(details.getName());
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
