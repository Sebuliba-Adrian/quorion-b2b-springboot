package com.quorion.b2b.service;

import com.quorion.b2b.model.product.ListPrice;
import com.quorion.b2b.repository.ListPriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ListPriceService {
    private final ListPriceRepository repository;

    @Transactional(readOnly = true)
    public List<ListPrice> getAll(UUID skuId, String currency, Boolean isActive) {
        if (skuId != null && currency != null && isActive != null) {
            return repository.findBySkuIdAndCurrencyAndIsActive(skuId, currency, isActive);
        } else if (skuId != null) {
            return repository.findBySkuId(skuId);
        } else if (currency != null) {
            return repository.findByCurrency(currency);
        } else if (isActive != null) {
            return repository.findByIsActive(isActive);
        }
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public ListPrice getById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("ListPrice not found"));
    }

    @Transactional
    public ListPrice create(ListPrice entity) {
        return repository.save(entity);
    }

    @Transactional
    public ListPrice update(UUID id, ListPrice details) {
        ListPrice entity = getById(id);
        if (details.getPrice() != null) entity.setPrice(details.getPrice());
        if (details.getCurrency() != null) entity.setCurrency(details.getCurrency());
        if (details.getEffectiveDate() != null) entity.setEffectiveDate(details.getEffectiveDate());
        if (details.getEndDate() != null) entity.setEndDate(details.getEndDate());
        if (details.getIsActive() != null) entity.setIsActive(details.getIsActive());
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
