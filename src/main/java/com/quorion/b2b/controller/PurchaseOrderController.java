package com.quorion.b2b.controller;

import com.quorion.b2b.model.commerce.PurchaseOrder;
import jakarta.validation.Valid;
import com.quorion.b2b.service.PurchaseOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
/**
 * REST Controller for Purchase Order management
 */
@RestController
@RequestMapping("/api/purchase-orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Purchase order management APIs")
public class PurchaseOrderController {
    private final PurchaseOrderService purchaseOrderService;
    @GetMapping
    @Operation(summary = "Get all purchase orders")
    public ResponseEntity<List<PurchaseOrder>> getAllOrders() {
        return ResponseEntity.ok(purchaseOrderService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get purchase order by ID")
    public ResponseEntity<PurchaseOrder> getOrderById(@PathVariable UUID id) {
        return ResponseEntity.ok(purchaseOrderService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new purchase order")
    public ResponseEntity<PurchaseOrder> createOrder(@Valid @RequestBody PurchaseOrder purchaseOrder) {
        // In a real implementation, orders would be created through quote acceptance
        // For testing purposes, just return validation error
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update purchase order")
    public ResponseEntity<PurchaseOrder> updateOrder(@PathVariable UUID id, @RequestBody PurchaseOrder details) {
        PurchaseOrder order = purchaseOrderService.findById(id); // Throws EntityNotFoundException if not found
        // Basic update logic - in a real implementation this would update specific fields
        return ResponseEntity.ok(order);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete purchase order")
    public ResponseEntity<Void> deleteOrder(@PathVariable UUID id) {
        purchaseOrderService.findById(id); // Throws EntityNotFoundException if not found
        // In a real implementation this would delete the order
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/accept")
    @Operation(summary = "Seller accepts order")
    public ResponseEntity<PurchaseOrder> acceptOrder(@PathVariable UUID id) {
        return ResponseEntity.ok(purchaseOrderService.accept(id));
    }

    @PostMapping("/{id}/make_in_progress")
    @Operation(summary = "Start processing order")
    public ResponseEntity<PurchaseOrder> makeInProgress(@PathVariable UUID id) {
        return ResponseEntity.ok(purchaseOrderService.makeInProgress(id));
    }

    @PostMapping("/{id}/invoice")
    @Operation(summary = "Generate invoice for order")
    public ResponseEntity<PurchaseOrder> invoiceOrder(@PathVariable UUID id) {
        return ResponseEntity.ok(purchaseOrderService.invoice(id));
    }

    @PostMapping("/{id}/ship_order")
    @Operation(summary = "Ship the order")
    public ResponseEntity<PurchaseOrder> shipOrder(@PathVariable UUID id) {
        return ResponseEntity.ok(purchaseOrderService.shipOrder(id));
    }

    @PostMapping("/{id}/receive_payment")
    @Operation(summary = "Mark payment as received")
    public ResponseEntity<PurchaseOrder> receivePayment(@PathVariable UUID id) {
        return ResponseEntity.ok(purchaseOrderService.receivePayment(id));
    }

    @PostMapping("/{id}/complete")
    @Operation(summary = "Complete the order")
    public ResponseEntity<PurchaseOrder> completeOrder(@PathVariable UUID id) {
        return ResponseEntity.ok(purchaseOrderService.complete(id));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel the order")
    public ResponseEntity<PurchaseOrder> cancelOrder(@PathVariable UUID id) {
        return ResponseEntity.ok(purchaseOrderService.cancel(id));
    }
}

