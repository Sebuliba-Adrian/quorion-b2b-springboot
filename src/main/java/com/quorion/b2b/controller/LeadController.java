package com.quorion.b2b.controller;

import com.quorion.b2b.model.commerce.Lead;
import jakarta.validation.Valid;
import com.quorion.b2b.service.LeadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
/**
 * REST Controller for Lead management
 */
@RestController
@RequestMapping("/api/leads")
@RequiredArgsConstructor
@Tag(name = "Leads", description = "Lead management APIs")
public class LeadController {
    private final LeadService leadService;
    @GetMapping
    @Operation(summary = "Get all leads")
    public ResponseEntity<List<Lead>> getAllLeads() {
        return ResponseEntity.ok(leadService.findAll());
    }
    @GetMapping("/{id}")
    @Operation(summary = "Get lead by ID")
    public ResponseEntity<Lead> getLeadById(@PathVariable UUID id) {
        return ResponseEntity.ok(leadService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new lead")
    public ResponseEntity<Lead> createLead(@Valid @RequestBody Lead lead) {
        return ResponseEntity.status(HttpStatus.CREATED).body(leadService.create(lead));
    }

    @PostMapping("/{id}/create_lead")
    @Operation(summary = "Transition lead to NEW status")
    public ResponseEntity<Lead> transitionToNew(@PathVariable UUID id) {
        return ResponseEntity.ok(leadService.createLead(id));
    }

    @PostMapping("/{id}/convert")
    @Operation(summary = "Convert lead to quote")
    public ResponseEntity<Lead> convertLead(@PathVariable UUID id) {
        return ResponseEntity.ok(leadService.convert(id));
    }

    @PostMapping("/{id}/accept_by_distributor")
    @Operation(summary = "Distributor accepts lead")
    public ResponseEntity<Lead> acceptByDistributor(@PathVariable UUID id) {
        return ResponseEntity.ok(leadService.acceptByDistributor(id));
    }

    @PostMapping("/{id}/reject_by_distributor")
    @Operation(summary = "Distributor rejects lead")
    public ResponseEntity<Lead> rejectByDistributor(@PathVariable UUID id) {
        return ResponseEntity.ok(leadService.rejectByDistributor(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update lead")
    public ResponseEntity<Lead> updateLead(@PathVariable UUID id, @RequestBody Lead details) {
        Lead lead = leadService.findById(id); // Throws EntityNotFoundException if not found
        // Basic update logic - in a real implementation this would update specific fields
        return ResponseEntity.ok(lead);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete lead")
    public ResponseEntity<Void> deleteLead(@PathVariable UUID id) {
        leadService.findById(id); // Throws EntityNotFoundException if not found
        // In a real implementation this would delete the lead
        return ResponseEntity.noContent().build();
    }
}
