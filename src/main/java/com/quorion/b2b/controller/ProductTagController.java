package com.quorion.b2b.controller;

import com.quorion.b2b.model.product.ProductTag;
import jakarta.validation.Valid;
import com.quorion.b2b.repository.ProductTagRepository;
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
 * ProductTag Controller
 */
@RestController
@RequestMapping("/api/product-tags")
@RequiredArgsConstructor
@Tag(name = "ProductTag", description = "ProductTag management")
public class ProductTagController {
    private final ProductTagRepository producttagRepository;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List all product-tags")
    public ResponseEntity<List<ProductTag>> getAll() {
        return ResponseEntity.ok(producttagRepository.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get producttag by ID")
    public ResponseEntity<ProductTag> getById(@PathVariable UUID id) {
        return producttagRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create producttag")
    public ResponseEntity<ProductTag> create(@Valid @RequestBody ProductTag producttag) {
        return ResponseEntity.status(HttpStatus.CREATED).body(producttagRepository.save(producttag));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update producttag")
    public ResponseEntity<ProductTag> update(@PathVariable UUID id, @RequestBody ProductTag details) {
        return producttagRepository.findById(id)
                .map(existing -> {
                    details.setId(id);
                    return ResponseEntity.ok(producttagRepository.save(details));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete producttag")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (producttagRepository.existsById(id)) {
            producttagRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
