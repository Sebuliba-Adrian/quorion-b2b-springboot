package com.quorion.b2b.controller;

import com.quorion.b2b.model.commerce.DeliveryTerm;
import jakarta.validation.Valid;
import com.quorion.b2b.service.DeliveryTermService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/api/delivery-terms")
@RequiredArgsConstructor
@Tag(name = "Delivery Term", description = "Delivery terms (FOB, CIF, etc.)")
public class DeliveryTermController {
    private final DeliveryTermService service;
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List all delivery terms")
    public ResponseEntity<List<DeliveryTerm>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }
    @GetMapping("/{id}")
    @Operation(summary = "Get delivery term by ID")
    public ResponseEntity<DeliveryTerm> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    @Operation(summary = "Create delivery term")
    public ResponseEntity<DeliveryTerm> create(@Valid @RequestBody DeliveryTerm entity) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(entity));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update delivery term")
    public ResponseEntity<DeliveryTerm> update(@PathVariable UUID id, @RequestBody DeliveryTerm entity) {
        return ResponseEntity.ok(service.update(id, entity));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete delivery term")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
