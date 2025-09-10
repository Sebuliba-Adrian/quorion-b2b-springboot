package com.quorion.b2b.controller;

import com.quorion.b2b.model.product.ProductAttribute;
import jakarta.validation.Valid;
import com.quorion.b2b.repository.ProductAttributeRepository;
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
 * ProductAttribute Controller
 */
@RestController
@RequestMapping("/api/product-attributes")
@RequiredArgsConstructor
@Tag(name = "ProductAttribute", description = "ProductAttribute management")
public class ProductAttributeController {
    private final ProductAttributeRepository productattributeRepository;
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List all product-attributes")
    public ResponseEntity<List<ProductAttribute>> getAll() {
        return ResponseEntity.ok(productattributeRepository.findAll());
    }
    @GetMapping("/{id}")
    @Operation(summary = "Get productattribute by ID")
    public ResponseEntity<ProductAttribute> getById(@PathVariable UUID id) {
        return productattributeRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create productattribute")
    public ResponseEntity<ProductAttribute> create(@Valid @RequestBody ProductAttribute productattribute) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productattributeRepository.save(productattribute));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update productattribute")
    public ResponseEntity<ProductAttribute> update(@PathVariable UUID id, @RequestBody ProductAttribute details) {
        return productattributeRepository.findById(id)
                .map(existing -> {
                    details.setId(id);
                    return ResponseEntity.ok(productattributeRepository.save(details));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete productattribute")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (productattributeRepository.existsById(id)) {
            productattributeRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
