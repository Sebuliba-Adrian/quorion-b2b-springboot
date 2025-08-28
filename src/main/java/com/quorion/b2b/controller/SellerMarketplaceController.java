package com.quorion.b2b.controller;

import com.quorion.b2b.model.tenant.SellerMarketplace;
import com.quorion.b2b.security.permissions.IsTenantUser;
import com.quorion.b2b.service.SellerMarketplaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * SellerMarketplace Controller
 */
@RestController
@RequestMapping("/api/seller-marketplaces")
@RequiredArgsConstructor
@Tag(name = "Seller Marketplace", description = "Seller-specific marketplace settings")
public class SellerMarketplaceController {

    private final SellerMarketplaceService sellerMarketplaceService;

    @GetMapping
    @IsTenantUser
    @Operation(summary = "List all seller marketplace configs")
    public ResponseEntity<List<SellerMarketplace>> getAllSellerMarketplaces(
            @RequestParam(required = false) UUID sellerId,
            @RequestParam(required = false) Boolean isActive) {
        return ResponseEntity.ok(sellerMarketplaceService.getAllSellerMarketplaces(sellerId, isActive));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get seller marketplace by ID")
    public ResponseEntity<SellerMarketplace> getSellerMarketplaceById(@PathVariable UUID id) {
        return ResponseEntity.ok(sellerMarketplaceService.getSellerMarketplaceById(id));
    }

    @GetMapping("/{id}/effective-settings")
    @Operation(summary = "Get effective settings for seller")
    public ResponseEntity<Map<String, Object>> getEffectiveSettings(@PathVariable UUID id) {
        return ResponseEntity.ok(sellerMarketplaceService.getEffectiveSettings(id));
    }

    @PostMapping
    @Operation(summary = "Create seller marketplace config")
    public ResponseEntity<SellerMarketplace> createSellerMarketplace(@Valid @RequestBody SellerMarketplace sellerMarketplace) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(sellerMarketplaceService.createSellerMarketplace(sellerMarketplace));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update seller marketplace config")
    public ResponseEntity<SellerMarketplace> updateSellerMarketplace(
            @PathVariable UUID id,
            @RequestBody SellerMarketplace sellerMarketplace) {
        return ResponseEntity.ok(sellerMarketplaceService.updateSellerMarketplace(id, sellerMarketplace));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete seller marketplace config")
    public ResponseEntity<Void> deleteSellerMarketplace(@PathVariable UUID id) {
        sellerMarketplaceService.deleteSellerMarketplace(id);
        return ResponseEntity.noContent().build();
    }
}
