package com.quorion.b2b.controller;

import com.quorion.b2b.model.tenant.TenantAssociation;
import jakarta.validation.Valid;
import com.quorion.b2b.security.permissions.IsTenantUser;
import com.quorion.b2b.service.TenantAssociationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

/**
 * TenantAssociation Controller
 */
@RestController
@RequestMapping("/api/tenant-associations")
@RequiredArgsConstructor
@Tag(name = "Tenant Association", description = "Seller-Buyer/Distributor associations")
public class TenantAssociationController {
    private final TenantAssociationService tenantAssociationService;

    @GetMapping
    @IsTenantUser
    @Operation(summary = "List all tenant associations")
    public ResponseEntity<List<TenantAssociation>> getAllAssociations(
            @RequestParam(required = false) UUID sellerId,
            @RequestParam(required = false) UUID buyerId,
            @RequestParam(required = false) Boolean isActive) {
        return ResponseEntity.ok(tenantAssociationService.getAllAssociations(sellerId, buyerId, isActive));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get association by ID")
    public ResponseEntity<TenantAssociation> getAssociationById(@PathVariable UUID id) {
        return ResponseEntity.ok(tenantAssociationService.getAssociationById(id));
    }

    @PostMapping
    @Operation(summary = "Create tenant association")
    public ResponseEntity<TenantAssociation> createAssociation(@Valid @RequestBody TenantAssociation association) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tenantAssociationService.createAssociation(association));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update tenant association")
    public ResponseEntity<TenantAssociation> updateAssociation(@PathVariable UUID id, @RequestBody TenantAssociation association) {
        return ResponseEntity.ok(tenantAssociationService.updateAssociation(id, association));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete tenant association")
    public ResponseEntity<Void> deleteAssociation(@PathVariable UUID id) {
        tenantAssociationService.deleteAssociation(id);
        return ResponseEntity.noContent().build();
    }
}
