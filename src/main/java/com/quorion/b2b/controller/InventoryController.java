package com.quorion.b2b.controller;

import com.quorion.b2b.model.product.Inventory;
import jakarta.validation.Valid;
import com.quorion.b2b.repository.InventoryRepository;
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
 * Inventory Controller
 */
@RestController
@RequestMapping("/api/inventories")
@RequiredArgsConstructor
@Tag(name = "Inventory", description = "Inventory management")
public class InventoryController {
    private final InventoryRepository inventoryRepository;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List all inventory")
    public ResponseEntity<List<Inventory>> getAll() {
        return ResponseEntity.ok(inventoryRepository.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get inventory by ID")
    public ResponseEntity<Inventory> getById(@PathVariable UUID id) {
        return inventoryRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create inventory")
    public ResponseEntity<Inventory> create(@Valid @RequestBody Inventory inventory) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventoryRepository.save(inventory));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update inventory")
    public ResponseEntity<Inventory> update(@PathVariable UUID id, @RequestBody Inventory details) {
        return inventoryRepository.findById(id)
                .map(existing -> {
                    details.setId(id);
                    return ResponseEntity.ok(inventoryRepository.save(details));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete inventory")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (inventoryRepository.existsById(id)) {
            inventoryRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
