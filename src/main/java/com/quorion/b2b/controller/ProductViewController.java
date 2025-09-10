package com.quorion.b2b.controller;

import com.quorion.b2b.model.commerce.ProductView;
import jakarta.validation.Valid;
import com.quorion.b2b.repository.ProductViewRepository;
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
 * ProductView Controller
 */
@RestController
@RequestMapping("/api/product-views")
@RequiredArgsConstructor
@Tag(name = "ProductView", description = "ProductView management")
public class ProductViewController {
    private final ProductViewRepository productviewRepository;
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List all product-views")
    public ResponseEntity<List<ProductView>> getAll() {
        return ResponseEntity.ok(productviewRepository.findAll());
    }
    @GetMapping("/{id}")
    @Operation(summary = "Get productview by ID")
    public ResponseEntity<ProductView> getById(@PathVariable UUID id) {
        return productviewRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create productview")
    public ResponseEntity<ProductView> create(@Valid @RequestBody ProductView productview) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productviewRepository.save(productview));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update productview")
    public ResponseEntity<ProductView> update(@PathVariable UUID id, @RequestBody ProductView details) {
        return productviewRepository.findById(id)
                .map(existing -> {
                    details.setId(id);
                    return ResponseEntity.ok(productviewRepository.save(details));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete productview")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (productviewRepository.existsById(id)) {
            productviewRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
