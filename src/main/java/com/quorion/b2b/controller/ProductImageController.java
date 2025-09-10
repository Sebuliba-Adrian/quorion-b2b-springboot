package com.quorion.b2b.controller;

import com.quorion.b2b.model.product.ProductImage;
import jakarta.validation.Valid;
import com.quorion.b2b.repository.ProductImageRepository;
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
 * ProductImage Controller
 */
@RestController
@RequestMapping("/api/product-images")
@RequiredArgsConstructor
@Tag(name = "ProductImage", description = "ProductImage management")
public class ProductImageController {
    private final ProductImageRepository productimageRepository;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List all product-images")
    public ResponseEntity<List<ProductImage>> getAll() {
        return ResponseEntity.ok(productimageRepository.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get productimage by ID")
    public ResponseEntity<ProductImage> getById(@PathVariable UUID id) {
        return productimageRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create productimage")
    public ResponseEntity<ProductImage> create(@Valid @RequestBody ProductImage productimage) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productimageRepository.save(productimage));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update productimage")
    public ResponseEntity<ProductImage> update(@PathVariable UUID id, @RequestBody ProductImage details) {
        return productimageRepository.findById(id)
                .map(existing -> {
                    details.setId(id);
                    return ResponseEntity.ok(productimageRepository.save(details));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete productimage")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (productimageRepository.existsById(id)) {
            productimageRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
