package com.quorion.b2b.controller;

import com.quorion.b2b.model.product.Product;
import jakarta.validation.Valid;
import com.quorion.b2b.model.product.ProductSKU;
import com.quorion.b2b.security.permissions.IsSeller;
import com.quorion.b2b.service.ProductService;
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
 * Product Controller
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Product", description = "Product catalog management")
public class ProductController {
    private final ProductService productService;
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List all products")
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID")
    public ResponseEntity<Product> getProductById(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PostMapping
    @IsSeller
    @Operation(summary = "Create product")
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(product));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update product")
    public ResponseEntity<Product> updateProduct(@PathVariable UUID id, @RequestBody Product product) {
        return ResponseEntity.ok(productService.updateProduct(id, product));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/create-sku")
    @Operation(summary = "Create SKU for product")
    public ResponseEntity<ProductSKU> createSKU(@PathVariable UUID id, @Valid @RequestBody ProductSKU sku) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createSKU(id, sku));
    }
}
