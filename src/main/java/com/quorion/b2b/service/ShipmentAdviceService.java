package com.quorion.b2b.service;

import com.quorion.b2b.model.commerce.ShipmentAdvice;
import com.quorion.b2b.repository.ShipmentAdviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShipmentAdviceService {
    private final ShipmentAdviceRepository repository;

    @Transactional(readOnly = true)
    public List<ShipmentAdvice> getAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public ShipmentAdvice getById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("ShipmentAdvice not found"));
    }

    @Transactional
    public ShipmentAdvice create(ShipmentAdvice entity) {
        return repository.save(entity);
    }

    @Transactional
    public ShipmentAdvice update(UUID id, ShipmentAdvice details) {
        ShipmentAdvice entity = getById(id);
        if (details.getNumber() != null) entity.setNumber(details.getNumber());
        if (details.getShipmentDate() != null) entity.setShipmentDate(details.getShipmentDate());
        if (details.getExpectedDeliveryDate() != null) entity.setExpectedDeliveryDate(details.getExpectedDeliveryDate());
        if (details.getActualDeliveryDate() != null) entity.setActualDeliveryDate(details.getActualDeliveryDate());
        if (details.getTrackingNumber() != null) entity.setTrackingNumber(details.getTrackingNumber());
        if (details.getCarrier() != null) entity.setCarrier(details.getCarrier());
        if (details.getNotes() != null) entity.setNotes(details.getNotes());
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
