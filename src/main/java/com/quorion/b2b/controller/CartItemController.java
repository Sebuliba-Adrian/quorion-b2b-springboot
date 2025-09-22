package com.quorion.b2b.controller;

import com.quorion.b2b.model.commerce.CartItem;
import jakarta.validation.Valid;
import com.quorion.b2b.repository.CartItemRepository;
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
 * CartItem Controller
 */
@RestController
@RequestMapping("/api/cart-items")
@RequiredArgsConstructor
@Tag(name = "CartItem", description = "CartItem management")
public class CartItemController {
    private final CartItemRepository cartitemRepository;
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List all cart-items")
    public ResponseEntity<List<CartItem>> getAll() {
        return ResponseEntity.ok(cartitemRepository.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get cartitem by ID")
    public ResponseEntity<CartItem> getById(@PathVariable UUID id) {
        return cartitemRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create cartitem")
    public ResponseEntity<CartItem> create(@Valid @RequestBody CartItem cartitem) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cartitemRepository.save(cartitem));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update cartitem")
    public ResponseEntity<CartItem> update(@PathVariable UUID id, @RequestBody CartItem details) {
        return cartitemRepository.findById(id)
                .map(existing -> {
                    details.setId(id);
                    return ResponseEntity.ok(cartitemRepository.save(details));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete cartitem")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (cartitemRepository.existsById(id)) {
            cartitemRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}

