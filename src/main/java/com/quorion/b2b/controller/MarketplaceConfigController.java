package com.quorion.b2b.controller;

import com.quorion.b2b.model.tenant.MarketplaceConfig;
import jakarta.validation.Valid;
import com.quorion.b2b.model.tenant.MarketplaceMode;
import com.quorion.b2b.service.MarketplaceConfigService;
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
 * MarketplaceConfig Controller
 */
@RestController
@RequestMapping("/api/marketplace-configs")
@RequiredArgsConstructor
@Tag(name = "Marketplace Config", description = "Global marketplace configuration")
public class MarketplaceConfigController {
    private final MarketplaceConfigService marketplaceConfigService;
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List all marketplace configs")
    public ResponseEntity<List<MarketplaceConfig>> getAllConfigs(
            @RequestParam(required = false) MarketplaceMode mode,
            @RequestParam(required = false) Boolean isActive) {
        return ResponseEntity.ok(marketplaceConfigService.getAllConfigs(mode, isActive));
    }
    @GetMapping("/active")
    @Operation(summary = "Get active marketplace config")
    public ResponseEntity<MarketplaceConfig> getActiveConfig() {
        return ResponseEntity.ok(marketplaceConfigService.getActiveConfig());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get config by ID")
    public ResponseEntity<MarketplaceConfig> getConfigById(@PathVariable UUID id) {
        return ResponseEntity.ok(marketplaceConfigService.getConfigById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create marketplace config")
    public ResponseEntity<MarketplaceConfig> createConfig(@Valid @RequestBody MarketplaceConfig config) {
        return ResponseEntity.status(HttpStatus.CREATED).body(marketplaceConfigService.createConfig(config));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update marketplace config")
    public ResponseEntity<MarketplaceConfig> updateConfig(@PathVariable UUID id, @RequestBody MarketplaceConfig config) {
        return ResponseEntity.ok(marketplaceConfigService.updateConfig(id, config));
    }

    @PostMapping("/{id}/activate")
    @Operation(summary = "Activate marketplace config")
    public ResponseEntity<MarketplaceConfig> activateConfig(@PathVariable UUID id) {
        return ResponseEntity.ok(marketplaceConfigService.activateConfig(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete marketplace config")
    public ResponseEntity<Void> deleteConfig(@PathVariable UUID id) {
        marketplaceConfigService.getConfigById(id); // Throws EntityNotFoundException if not found
        marketplaceConfigService.deleteConfig(id);
        return ResponseEntity.noContent().build();
    }
}
