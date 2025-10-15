package com.quorion.b2b.controller;

import com.quorion.b2b.model.commerce.SellerRating;
import jakarta.validation.Valid;
import com.quorion.b2b.repository.SellerRatingRepository;
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
 * SellerRating Controller
 */
@RestController
@RequestMapping("/api/seller-ratings")
@RequiredArgsConstructor
@Tag(name = "SellerRating", description = "SellerRating management")
public class SellerRatingController {
    private final SellerRatingRepository sellerratingRepository;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List all seller-ratings")
    public ResponseEntity<List<SellerRating>> getAll() {
        return ResponseEntity.ok(sellerratingRepository.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get sellerrating by ID")
    public ResponseEntity<SellerRating> getById(@PathVariable UUID id) {
        return sellerratingRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create sellerrating")
    public ResponseEntity<SellerRating> create(@Valid @RequestBody SellerRating sellerrating) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sellerratingRepository.save(sellerrating));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update sellerrating")
    public ResponseEntity<SellerRating> update(@PathVariable UUID id, @RequestBody SellerRating details) {
        return sellerratingRepository.findById(id)
                .map(existing -> {
                    details.setId(id);
                    return ResponseEntity.ok(sellerratingRepository.save(details));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete sellerrating")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (sellerratingRepository.existsById(id)) {
            sellerratingRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
