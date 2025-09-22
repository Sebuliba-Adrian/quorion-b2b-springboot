package com.quorion.b2b.controller;

import com.quorion.b2b.model.commerce.DirectCheckout;
import jakarta.validation.Valid;
import com.quorion.b2b.repository.DirectCheckoutRepository;
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
 * DirectCheckout Controller
 */
@RestController
@RequestMapping("/api/direct-checkouts")
@RequiredArgsConstructor
@Tag(name = "DirectCheckout", description = "DirectCheckout management")
public class DirectCheckoutController {
    private final DirectCheckoutRepository directcheckoutRepository;
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List all direct-checkouts")
    public ResponseEntity<List<DirectCheckout>> getAll() {
        return ResponseEntity.ok(directcheckoutRepository.findAll());
    }
    @GetMapping("/{id}")
    @Operation(summary = "Get directcheckout by ID")
    public ResponseEntity<DirectCheckout> getById(@PathVariable UUID id) {
        return directcheckoutRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create directcheckout")
    public ResponseEntity<DirectCheckout> create(@Valid @RequestBody DirectCheckout directcheckout) {
        return ResponseEntity.status(HttpStatus.CREATED).body(directcheckoutRepository.save(directcheckout));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update directcheckout")
    public ResponseEntity<DirectCheckout> update(@PathVariable UUID id, @RequestBody DirectCheckout details) {
        return directcheckoutRepository.findById(id)
                .map(existing -> {
                    details.setId(id);
                    return ResponseEntity.ok(directcheckoutRepository.save(details));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete directcheckout")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (directcheckoutRepository.existsById(id)) {
            directcheckoutRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
