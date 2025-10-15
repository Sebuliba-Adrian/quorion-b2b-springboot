package com.quorion.b2b.controller;

import com.quorion.b2b.model.product.Wishlist;
import jakarta.validation.Valid;
import com.quorion.b2b.repository.WishlistRepository;
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
 * Wishlist Controller
 */
@RestController
@RequestMapping("/api/wishlists")
@RequiredArgsConstructor
@Tag(name = "Wishlist", description = "Wishlist management")
public class WishlistController {
    private final WishlistRepository wishlistRepository;
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List all wishlists")
    public ResponseEntity<List<Wishlist>> getAll() {
        return ResponseEntity.ok(wishlistRepository.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get wishlist by ID")
    public ResponseEntity<Wishlist> getById(@PathVariable UUID id) {
        return wishlistRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create wishlist")
    public ResponseEntity<Wishlist> create(@Valid @RequestBody Wishlist wishlist) {
        return ResponseEntity.status(HttpStatus.CREATED).body(wishlistRepository.save(wishlist));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update wishlist")
    public ResponseEntity<Wishlist> update(@PathVariable UUID id, @RequestBody Wishlist details) {
        return wishlistRepository.findById(id)
                .map(existing -> {
                    details.setId(id);
                    return ResponseEntity.ok(wishlistRepository.save(details));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete wishlist")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (wishlistRepository.existsById(id)) {
            wishlistRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}

