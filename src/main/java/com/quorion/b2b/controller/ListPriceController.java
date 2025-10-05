package com.quorion.b2b.controller;

import com.quorion.b2b.model.product.ListPrice;
import jakarta.validation.Valid;
import com.quorion.b2b.service.ListPriceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/api/list-prices")
@RequiredArgsConstructor
@Tag(name = "List Price", description = "Product pricing management")
public class ListPriceController {
    private final ListPriceService service;
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List all prices")
    public ResponseEntity<List<ListPrice>> getAll(
            @RequestParam(required = false) UUID skuId,
            @RequestParam(required = false) String currency,
            @RequestParam(required = false) Boolean isActive) {
        return ResponseEntity.ok(service.getAll(skuId, currency, isActive));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get price by ID")
    public ResponseEntity<ListPrice> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    @Operation(summary = "Create price")
    public ResponseEntity<ListPrice> create(@Valid @RequestBody ListPrice entity) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(entity));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update price")
    public ResponseEntity<ListPrice> update(@PathVariable UUID id, @RequestBody ListPrice entity) {
        return ResponseEntity.ok(service.update(id, entity));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete price")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
