package com.quorion.b2b.controller;

import com.quorion.b2b.model.product.ProductReview;
import jakarta.validation.Valid;
import com.quorion.b2b.repository.ProductReviewRepository;
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
 * ProductReview Controller
 */
@RestController
@RequestMapping("/api/product-reviews")
@RequiredArgsConstructor
@Tag(name = "ProductReview", description = "ProductReview management")
public class ProductReviewController {
    private final ProductReviewRepository productreviewRepository;
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List all product-reviews")
    public ResponseEntity<List<ProductReview>> getAll() {
        return ResponseEntity.ok(productreviewRepository.findAll());
    }
    @GetMapping("/{id}")
    @Operation(summary = "Get productreview by ID")
    public ResponseEntity<ProductReview> getById(@PathVariable UUID id) {
        return productreviewRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create productreview")
    public ResponseEntity<ProductReview> create(@Valid @RequestBody ProductReview productreview) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productreviewRepository.save(productreview));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update productreview")
    public ResponseEntity<ProductReview> update(@PathVariable UUID id, @RequestBody ProductReview details) {
        return productreviewRepository.findById(id)
                .map(existing -> {
                    details.setId(id);
                    return ResponseEntity.ok(productreviewRepository.save(details));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete productreview")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (productreviewRepository.existsById(id)) {
            productreviewRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
