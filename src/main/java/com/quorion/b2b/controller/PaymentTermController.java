package com.quorion.b2b.controller;

import com.quorion.b2b.model.commerce.PaymentTerm;
import jakarta.validation.Valid;
import com.quorion.b2b.service.PaymentTermService;
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
@RequestMapping("/api/payment-terms")
@RequiredArgsConstructor
@Tag(name = "Payment Term", description = "Payment terms (Net 30, Net 60, etc.)")
public class PaymentTermController {
    private final PaymentTermService service;
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List all payment terms")
    public ResponseEntity<List<PaymentTerm>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment term by ID")
    public ResponseEntity<PaymentTerm> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    @Operation(summary = "Create payment term")
    public ResponseEntity<PaymentTerm> create(@Valid @RequestBody PaymentTerm entity) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(entity));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update payment term")
    public ResponseEntity<PaymentTerm> update(@PathVariable UUID id, @RequestBody PaymentTerm entity) {
        return ResponseEntity.ok(service.update(id, entity));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete payment term")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
