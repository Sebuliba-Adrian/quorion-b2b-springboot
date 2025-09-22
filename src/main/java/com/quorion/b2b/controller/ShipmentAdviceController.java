package com.quorion.b2b.controller;

import com.quorion.b2b.model.commerce.ShipmentAdvice;
import com.quorion.b2b.service.ShipmentAdviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/shipment-advices")
@RequiredArgsConstructor
@Tag(name = "ShipmentAdvice", description = "Shipment advice management")
public class ShipmentAdviceController {

    private final ShipmentAdviceService service;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List all shipment advices")
    public ResponseEntity<List<ShipmentAdvice>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get shipment advice by ID")
    public ResponseEntity<ShipmentAdvice> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    @Operation(summary = "Create shipment advice")
    public ResponseEntity<ShipmentAdvice> create(@Valid @RequestBody ShipmentAdvice entity) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(entity));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update shipment advice")
    public ResponseEntity<ShipmentAdvice> update(@PathVariable UUID id, @RequestBody ShipmentAdvice entity) {
        return ResponseEntity.ok(service.update(id, entity));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete shipment advice")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
