package com.quorion.b2b.controller;

import com.quorion.b2b.model.product.ProductAttributeValue;
import jakarta.validation.Valid;
import com.quorion.b2b.repository.ProductAttributeValueRepository;
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
 * ProductAttributeValue Controller
 */
@RestController
@RequestMapping("/api/product-attribute-values")
@RequiredArgsConstructor
@Tag(name = "ProductAttributeValue", description = "ProductAttributeValue management")
public class ProductAttributeValueController {
    private final ProductAttributeValueRepository productattributevalueRepository;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List all product-attribute-values")
    public ResponseEntity<List<ProductAttributeValue>> getAll() {
        return ResponseEntity.ok(productattributevalueRepository.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get productattributevalue by ID")
    public ResponseEntity<ProductAttributeValue> getById(@PathVariable UUID id) {
        return productattributevalueRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create productattributevalue")
    public ResponseEntity<ProductAttributeValue> create(@Valid @RequestBody ProductAttributeValue productattributevalue) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productattributevalueRepository.save(productattributevalue));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update productattributevalue")
    public ResponseEntity<ProductAttributeValue> update(@PathVariable UUID id, @RequestBody ProductAttributeValue details) {
        return productattributevalueRepository.findById(id)
                .map(existing -> {
                    details.setId(id);
                    return ResponseEntity.ok(productattributevalueRepository.save(details));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete productattributevalue")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (productattributevalueRepository.existsById(id)) {
            productattributevalueRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
