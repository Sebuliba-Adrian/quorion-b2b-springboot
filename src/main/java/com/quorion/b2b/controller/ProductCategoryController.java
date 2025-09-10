package com.quorion.b2b.controller;

import com.quorion.b2b.model.product.ProductCategory;
import jakarta.validation.Valid;
import com.quorion.b2b.repository.ProductCategoryRepository;
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
 * ProductCategory Controller
 */
@RestController
@RequestMapping("/api/product-categories")
@RequiredArgsConstructor
@Tag(name = "ProductCategory", description = "ProductCategory management")
public class ProductCategoryController {
    private final ProductCategoryRepository productcategoryRepository;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List all product-categories")
    public ResponseEntity<List<ProductCategory>> getAll() {
        return ResponseEntity.ok(productcategoryRepository.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get productcategory by ID")
    public ResponseEntity<ProductCategory> getById(@PathVariable UUID id) {
        return productcategoryRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create productcategory")
    public ResponseEntity<ProductCategory> create(@Valid @RequestBody ProductCategory productcategory) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productcategoryRepository.save(productcategory));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update productcategory")
    public ResponseEntity<ProductCategory> update(@PathVariable UUID id, @RequestBody ProductCategory details) {
        return productcategoryRepository.findById(id)
                .map(existing -> {
                    details.setId(id);
                    return ResponseEntity.ok(productcategoryRepository.save(details));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete productcategory")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (productcategoryRepository.existsById(id)) {
            productcategoryRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
