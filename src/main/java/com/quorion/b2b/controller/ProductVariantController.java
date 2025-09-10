package com.quorion.b2b.controller;

import com.quorion.b2b.model.product.ProductVariant;
import jakarta.validation.Valid;
import com.quorion.b2b.repository.ProductVariantRepository;
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
 * ProductVariant Controller
 */
@RestController
@RequestMapping("/api/product-variants")
@RequiredArgsConstructor
@Tag(name = "ProductVariant", description = "ProductVariant management")
public class ProductVariantController {
    private final ProductVariantRepository productvariantRepository;
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List all product-variants")
    public ResponseEntity<List<ProductVariant>> getAll() {
        return ResponseEntity.ok(productvariantRepository.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get productvariant by ID")
    public ResponseEntity<ProductVariant> getById(@PathVariable UUID id) {
        return productvariantRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create productvariant")
    public ResponseEntity<ProductVariant> create(@Valid @RequestBody ProductVariant productvariant) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productvariantRepository.save(productvariant));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update productvariant")
    public ResponseEntity<ProductVariant> update(@PathVariable UUID id, @RequestBody ProductVariant details) {
        return productvariantRepository.findById(id)
                .map(existing -> {
                    details.setId(id);
                    return ResponseEntity.ok(productvariantRepository.save(details));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete productvariant")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (productvariantRepository.existsById(id)) {
            productvariantRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
