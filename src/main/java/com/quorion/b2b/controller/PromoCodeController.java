package com.quorion.b2b.controller;

import com.quorion.b2b.model.commerce.PromoCode;
import jakarta.validation.Valid;
import com.quorion.b2b.repository.PromoCodeRepository;
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
 * PromoCode Controller
 */
@RestController
@RequestMapping("/api/promo-codes")
@RequiredArgsConstructor
@Tag(name = "PromoCode", description = "PromoCode management")
public class PromoCodeController {
    private final PromoCodeRepository promocodeRepository;
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List all promo-codes")
    public ResponseEntity<List<PromoCode>> getAll() {
        return ResponseEntity.ok(promocodeRepository.findAll());
    }
    @GetMapping("/{id}")
    @Operation(summary = "Get promocode by ID")
    public ResponseEntity<PromoCode> getById(@PathVariable UUID id) {
        return promocodeRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create promocode")
    public ResponseEntity<PromoCode> create(@Valid @RequestBody PromoCode promocode) {
        return ResponseEntity.status(HttpStatus.CREATED).body(promocodeRepository.save(promocode));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update promocode")
    public ResponseEntity<PromoCode> update(@PathVariable UUID id, @RequestBody PromoCode details) {
        return promocodeRepository.findById(id)
                .map(existing -> {
                    details.setId(id);
                    return ResponseEntity.ok(promocodeRepository.save(details));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete promocode")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (promocodeRepository.existsById(id)) {
            promocodeRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
