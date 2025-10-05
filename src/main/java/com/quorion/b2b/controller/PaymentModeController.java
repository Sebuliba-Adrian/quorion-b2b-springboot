package com.quorion.b2b.controller;

import com.quorion.b2b.model.commerce.PaymentMode;
import jakarta.validation.Valid;
import com.quorion.b2b.service.PaymentModeService;
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
@RequestMapping("/api/payment-modes")
@RequiredArgsConstructor
@Tag(name = "Payment Mode", description = "Payment modes (Cash, Credit Card, Bank Transfer, etc.)")
public class PaymentModeController {
    private final PaymentModeService service;
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List all payment modes")
    public ResponseEntity<List<PaymentMode>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }
    @GetMapping("/{id}")
    @Operation(summary = "Get payment mode by ID")
    public ResponseEntity<PaymentMode> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    @Operation(summary = "Create payment mode")
    public ResponseEntity<PaymentMode> create(@Valid @RequestBody PaymentMode entity) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(entity));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update payment mode")
    public ResponseEntity<PaymentMode> update(@PathVariable UUID id, @RequestBody PaymentMode entity) {
        return ResponseEntity.ok(service.update(id, entity));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete payment mode")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
