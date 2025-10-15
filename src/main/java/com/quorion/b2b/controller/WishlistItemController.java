package com.quorion.b2b.controller;

import com.quorion.b2b.model.product.WishlistItem;
import jakarta.validation.Valid;
import com.quorion.b2b.repository.WishlistItemRepository;
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
 * WishlistItem Controller
 */
@RestController
@RequestMapping("/api/wishlist-items")
@RequiredArgsConstructor
@Tag(name = "WishlistItem", description = "WishlistItem management")
public class WishlistItemController {
    private final WishlistItemRepository wishlistitemRepository;
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List all wishlist-items")
    public ResponseEntity<List<WishlistItem>> getAll() {
        return ResponseEntity.ok(wishlistitemRepository.findAll());
    }
    @GetMapping("/{id}")
    @Operation(summary = "Get wishlistitem by ID")
    public ResponseEntity<WishlistItem> getById(@PathVariable UUID id) {
        return wishlistitemRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create wishlistitem")
    public ResponseEntity<WishlistItem> create(@Valid @RequestBody WishlistItem wishlistitem) {
        return ResponseEntity.status(HttpStatus.CREATED).body(wishlistitemRepository.save(wishlistitem));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update wishlistitem")
    public ResponseEntity<WishlistItem> update(@PathVariable UUID id, @RequestBody WishlistItem details) {
        return wishlistitemRepository.findById(id)
                .map(existing -> {
                    details.setId(id);
                    return ResponseEntity.ok(wishlistitemRepository.save(details));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete wishlistitem")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (wishlistitemRepository.existsById(id)) {
            wishlistitemRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
