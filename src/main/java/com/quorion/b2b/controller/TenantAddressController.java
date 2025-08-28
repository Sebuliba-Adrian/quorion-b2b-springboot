package com.quorion.b2b.controller;

import com.quorion.b2b.model.tenant.TenantAddress;
import jakarta.validation.Valid;
import com.quorion.b2b.model.tenant.AddressType;
import com.quorion.b2b.security.permissions.IsTenantUser;
import com.quorion.b2b.service.TenantAddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

/**
 * TenantAddress Controller
 */
@RestController
@RequestMapping("/api/tenant-addresses")
@RequiredArgsConstructor
@Tag(name = "Tenant Address", description = "Tenant address management")
public class TenantAddressController {
    private final TenantAddressService tenantAddressService;

    @GetMapping
    @IsTenantUser
    @Operation(summary = "List all tenant addresses")
    public ResponseEntity<List<TenantAddress>> getAllAddresses(
            @RequestParam(required = false) UUID tenantId,
            @RequestParam(required = false) AddressType addressType,
            @RequestParam(required = false) Boolean isActive) {
        return ResponseEntity.ok(tenantAddressService.getAllAddresses(tenantId, addressType, isActive));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get address by ID")
    public ResponseEntity<TenantAddress> getAddressById(@PathVariable UUID id) {
        return ResponseEntity.ok(tenantAddressService.getAddressById(id));
    }

    @PostMapping
    @Operation(summary = "Create tenant address")
    public ResponseEntity<TenantAddress> createAddress(@Valid @RequestBody TenantAddress address) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tenantAddressService.createAddress(address));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update tenant address")
    public ResponseEntity<TenantAddress> updateAddress(@PathVariable UUID id, @RequestBody TenantAddress address) {
        return ResponseEntity.ok(tenantAddressService.updateAddress(id, address));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete tenant address")
    public ResponseEntity<Void> deleteAddress(@PathVariable UUID id) {
        tenantAddressService.deleteAddress(id);
        return ResponseEntity.noContent().build();
    }
}
