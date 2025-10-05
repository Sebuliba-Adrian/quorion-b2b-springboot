package com.quorion.b2b.controller;

import com.quorion.b2b.model.product.PackagingUnit;
import jakarta.validation.Valid;
import com.quorion.b2b.service.PackagingUnitService;
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
@RequestMapping("/api/packaging-units")
@RequiredArgsConstructor
@Tag(name = "Packaging Unit", description = "Packaging unit management")
public class PackagingUnitController {
    private final PackagingUnitService service;
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List all packaging units")
    public ResponseEntity<List<PackagingUnit>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }
    @GetMapping("/{id}")
    @Operation(summary = "Get packaging unit by ID")
    public ResponseEntity<PackagingUnit> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    @Operation(summary = "Create packaging unit")
    public ResponseEntity<PackagingUnit> create(@Valid @RequestBody PackagingUnit entity) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(entity));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update packaging unit")
    public ResponseEntity<PackagingUnit> update(@PathVariable UUID id, @RequestBody PackagingUnit entity) {
        return ResponseEntity.ok(service.update(id, entity));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete packaging unit")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
