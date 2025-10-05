package com.quorion.b2b.controller;

import com.quorion.b2b.model.product.PackagingType;
import jakarta.validation.Valid;
import com.quorion.b2b.service.PackagingTypeService;
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
@RequestMapping("/api/packaging-types")
@RequiredArgsConstructor
@Tag(name = "Packaging Type", description = "Packaging type management")
public class PackagingTypeController {
    private final PackagingTypeService service;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List all packaging types")
    public ResponseEntity<List<PackagingType>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get packaging type by ID")
    public ResponseEntity<PackagingType> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    @Operation(summary = "Create packaging type")
    public ResponseEntity<PackagingType> create(@Valid @RequestBody PackagingType entity) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(entity));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update packaging type")
    public ResponseEntity<PackagingType> update(@PathVariable UUID id, @RequestBody PackagingType entity) {
        return ResponseEntity.ok(service.update(id, entity));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete packaging type")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
