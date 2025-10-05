package com.quorion.b2b.controller;

import com.quorion.b2b.model.commerce.PriceTier;
import com.quorion.b2b.service.PriceTierService;
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
@RequestMapping("/api/price-tiers")
@RequiredArgsConstructor
@Tag(name = "PriceTier", description = "Price tier management")
public class PriceTierController {

    private final PriceTierService service;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List all price tiers")
    public ResponseEntity<List<PriceTier>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get price tier by ID")
    public ResponseEntity<PriceTier> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    @Operation(summary = "Create price tier")
    public ResponseEntity<PriceTier> create(@Valid @RequestBody PriceTier entity) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(entity));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update price tier")
    public ResponseEntity<PriceTier> update(@PathVariable UUID id, @RequestBody PriceTier entity) {
        return ResponseEntity.ok(service.update(id, entity));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete price tier")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
