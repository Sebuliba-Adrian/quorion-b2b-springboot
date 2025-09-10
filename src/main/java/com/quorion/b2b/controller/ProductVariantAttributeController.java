package com.quorion.b2b.controller;

import com.quorion.b2b.model.product.ProductVariantAttribute;
import jakarta.validation.Valid;
import com.quorion.b2b.repository.ProductVariantAttributeRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

/**
 * ProductVariantAttribute Controller
 */
@RestController
@RequestMapping("/api/product-variant-attributes")
@RequiredArgsConstructor
@Tag(name = "ProductVariantAttribute", description = "Product variant attribute management")
public class ProductVariantAttributeController {
    private final ProductVariantAttributeRepository repository;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List all product variant attributes")
    public ResponseEntity<List<ProductVariantAttribute>> getAll() {
        return ResponseEntity.ok(repository.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product variant attribute by ID")
    public ResponseEntity<ProductVariantAttribute> getById(@PathVariable UUID id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create product variant attribute")
    public ResponseEntity<ProductVariantAttribute> create(@Valid @RequestBody ProductVariantAttribute entity) {
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(entity));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update product variant attribute")
    public ResponseEntity<ProductVariantAttribute> update(@PathVariable UUID id, @RequestBody ProductVariantAttribute details) {
        return repository.findById(id)
                .map(existing -> {
                    details.setId(id);
                    return ResponseEntity.ok(repository.save(details));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product variant attribute")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
