package com.quorion.b2b.controller;

import com.quorion.b2b.model.tenant.Tenant;
import com.quorion.b2b.model.tenant.TenantAddress;
import com.quorion.b2b.security.permissions.IsTenantUser;
import com.quorion.b2b.service.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
/**
 * Tenant Controller
 * Manages tenants (sellers, buyers, distributors)
 */
@RestController
@RequestMapping("/api/tenants")
@RequiredArgsConstructor
@Tag(name = "Tenant", description = "Tenant management for sellers, buyers, and distributors")
public class TenantController {
    private final TenantService tenantService;
    /**
     * Get all tenants
     * GET /api/tenants
     */
    @GetMapping
    @IsTenantUser
    @Operation(summary = "List all tenants", description = "Get all tenants in the system")
    public ResponseEntity<List<Tenant>> getAllTenants() {
        List<Tenant> tenants = tenantService.getAllTenants();
        return ResponseEntity.ok(tenants);
    }

    /**
     * Get tenant by ID
     * GET /api/tenants/{id}
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get tenant by ID", description = "Retrieve a specific tenant by ID")
    public ResponseEntity<Tenant> getTenantById(@PathVariable UUID id) {
        Tenant tenant = tenantService.getTenantById(id);
        return ResponseEntity.ok(tenant);
    }

    /**
     * Create new tenant
     * POST /api/tenants
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create tenant", description = "Create a new tenant (admin only)")
    public ResponseEntity<Tenant> createTenant(@Valid @RequestBody Tenant tenant) {
        Tenant created = tenantService.createTenant(tenant);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Update tenant
     * PUT /api/tenants/{id}
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update tenant", description = "Update tenant information (admin only)")
    public ResponseEntity<Tenant> updateTenant(
            @PathVariable UUID id,
            @RequestBody Tenant tenant) {
        Tenant updated = tenantService.updateTenant(id, tenant);
        return ResponseEntity.ok(updated);
    }

    /**
     * Delete tenant
     * DELETE /api/tenants/{id}
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete tenant", description = "Delete a tenant (admin only)")
    public ResponseEntity<Void> deleteTenant(@PathVariable UUID id) {
        tenantService.deleteTenant(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get tenant addresses
     * GET /api/tenants/{id}/addresses
     */
    @GetMapping("/{id}/addresses")
    @Operation(summary = "Get tenant addresses", description = "Get all addresses for a tenant")
    public ResponseEntity<List<TenantAddress>> getTenantAddresses(@PathVariable UUID id) {
        List<TenantAddress> addresses = tenantService.getTenantAddresses(id);
        return ResponseEntity.ok(addresses);
    }

    /**
     * Get distributors for a seller
     * GET /api/tenants/{id}/distributors
     */
    @GetMapping("/{id}/distributors")
    @Operation(summary = "Get seller distributors", description = "Get all distributors associated with a seller tenant")
    public ResponseEntity<List<Tenant>> getDistributors(@PathVariable UUID id) {
        List<Tenant> distributors = tenantService.getDistributors(id);
        return ResponseEntity.ok(distributors);
    }
}
