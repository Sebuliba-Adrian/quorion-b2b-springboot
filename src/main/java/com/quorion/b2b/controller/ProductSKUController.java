package com.quorion.b2b.controller;

import com.quorion.b2b.model.product.ProductSKU;
import com.quorion.b2b.service.ProductSKUService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

/**
 * ProductSKU Controller
 */
@RestController
@RequestMapping("/api/product-skus")
@RequiredArgsConstructor
@Tag(name = "Product SKU", description = "Product SKU management")
public class ProductSKUController {
    private final ProductSKUService productSKUService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List all SKUs")
    public ResponseEntity<List<ProductSKU>> getAllSKUs() {
        return ResponseEntity.ok(productSKUService.getAllSKUs());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get SKU by ID")
    public ResponseEntity<ProductSKU> getSKUById(@PathVariable UUID id) {
        return ResponseEntity.ok(productSKUService.getSKUById(id));
    }

    @PostMapping
    @Operation(summary = "Create SKU")
    public ResponseEntity<ProductSKU> createSKU(@Valid @RequestBody ProductSKU sku) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productSKUService.createSKU(sku));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update SKU")
    public ResponseEntity<ProductSKU> updateSKU(@PathVariable UUID id, @RequestBody ProductSKU sku) {
        return ResponseEntity.ok(productSKUService.updateSKU(id, sku));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete SKU")
    public ResponseEntity<Void> deleteSKU(@PathVariable UUID id) {
        productSKUService.deleteSKU(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/create-distributor-copy")
    @Operation(summary = "Create distributor copy of SKU")
    public ResponseEntity<ProductSKU> createDistributorCopy(
            @PathVariable UUID id,
            @Valid @RequestBody CreateDistributorCopyRequest request) {
        ProductSKU distributorSku = productSKUService.createDistributorCopy(id, request.getDistributorId());
        return ResponseEntity.status(HttpStatus.CREATED).body(distributorSku);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateDistributorCopyRequest {
        @NotNull(message = "Distributor ID is required")
        private UUID distributorId;
    }
}
