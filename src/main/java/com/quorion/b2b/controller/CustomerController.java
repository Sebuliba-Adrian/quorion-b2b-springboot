package com.quorion.b2b.controller;

import com.quorion.b2b.model.commerce.Customer;
import jakarta.validation.Valid;
import com.quorion.b2b.security.permissions.IsTenantUser;
import com.quorion.b2b.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Tag(name = "Customer", description = "Customer management")
public class CustomerController {
    private final CustomerService service;
    @GetMapping
    @IsTenantUser
    @Operation(summary = "List all customers")
    public ResponseEntity<List<Customer>> getAll(
            @RequestParam(required = false) UUID tenantId,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Boolean isActive) {
        return ResponseEntity.ok(service.getAll(tenantId, email, isActive));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get customer by ID")
    public ResponseEntity<Customer> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    @Operation(summary = "Create customer")
    public ResponseEntity<Customer> create(@Valid @RequestBody Customer entity) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(entity));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update customer")
    public ResponseEntity<Customer> update(@PathVariable UUID id, @RequestBody Customer entity) {
        return ResponseEntity.ok(service.update(id, entity));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete customer")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
