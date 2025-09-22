package com.quorion.b2b.controller;

import com.quorion.b2b.model.commerce.Cart;
import com.quorion.b2b.model.commerce.CartItem;
import com.quorion.b2b.model.commerce.Lead;
import com.quorion.b2b.security.permissions.IsBuyer;
import com.quorion.b2b.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Cart Controller
 * Manages shopping carts with advanced features like bulk operations, cloning, and lead conversion
 */
@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
@Tag(name = "Cart", description = "Shopping cart management with guest and authenticated user support")
public class CartController {
    private final CartService cartService;

    /**
     * Get all carts (with optional filtering)
     * GET /api/carts
     */
    @GetMapping
    @Operation(summary = "List all carts", description = "Get all carts with optional filtering by buyer and active status")
    public ResponseEntity<List<Cart>> getAllCarts(
            @RequestParam(required = false) UUID buyerId,
            @RequestParam(required = false) Boolean isActive) {
        List<Cart> carts = cartService.getAllCarts(buyerId, isActive);
        return ResponseEntity.ok(carts);
    }

    /**
     * Get cart by ID
     * GET /api/carts/{id}
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get cart by ID", description = "Retrieve a specific cart by its ID")
    public ResponseEntity<Cart> getCartById(@PathVariable UUID id) {
        Cart cart = cartService.getCartById(id);
        return ResponseEntity.ok(cart);
    }

    /**
     * Create new cart
     * POST /api/carts
     */
    @PostMapping
    @Operation(summary = "Create cart", description = "Create a new shopping cart (supports guest and authenticated users)")
    public ResponseEntity<Cart> createCart(@Valid @RequestBody CreateCartRequest request) {
        // Validate that at least buyerId or customerId is provided
        if (request.getBuyerId() == null && request.getCustomerId() == null) {
            throw new IllegalArgumentException("Either buyerId or customerId must be provided");
        }
        Cart cart = cartService.createCart(request.getBuyerId(), request.getCustomerId());
        return ResponseEntity.status(HttpStatus.CREATED).body(cart);
    }

    /**
     * Update cart
     * PUT /api/carts/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update cart", description = "Update cart buyer, customer, or active status")
    public ResponseEntity<Cart> updateCart(
            @PathVariable UUID id,
            @RequestBody UpdateCartRequest request) {
        Cart cart = cartService.updateCart(id, request.getBuyerId(), request.getCustomerId(), request.getIsActive());
        return ResponseEntity.ok(cart);
    }

    /**
     * Delete cart
     * DELETE /api/carts/{id}
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete cart", description = "Delete a cart permanently")
    public ResponseEntity<Void> deleteCart(@PathVariable UUID id) {
        cartService.deleteCart(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Add item to cart
     * POST /api/carts/{id}/add-item
     */
    @PostMapping("/{id}/add-item")
    @Operation(summary = "Add item to cart", description = "Add a new item or update existing item quantity in cart")
    public ResponseEntity<CartItem> addItem(
            @PathVariable UUID id,
            @Valid @RequestBody AddItemRequest request) {
        CartItem item = cartService.addItem(
                id,
                request.getProductId(),
                request.getQuantity(),
                request.getUnitPrice(),
                request.getNotes()
        );
        return ResponseEntity.ok(item);
    }

    /**
     * Remove item from cart
     * POST /api/carts/{id}/remove-item
     */
    @PostMapping("/{id}/remove-item")
    @Operation(summary = "Remove item from cart", description = "Soft delete an item from the cart")
    public ResponseEntity<Map<String, String>> removeItem(
            @PathVariable UUID id,
            @Valid @RequestBody RemoveItemRequest request) {
        cartService.removeItem(id, request.getItemId());
        return ResponseEntity.ok(Map.of("message", "Item removed from cart"));
    }

    /**
     * Clear cart
     * POST /api/carts/{id}/clear
     */
    @PostMapping("/{id}/clear")
    @Operation(summary = "Clear cart", description = "Remove all items from cart")
    public ResponseEntity<Map<String, String>> clearCart(@PathVariable UUID id) {
        cartService.clearCart(id);
        return ResponseEntity.ok(Map.of("message", "Cart cleared"));
    }

    /**
     * Add bulk items to cart
     * POST /api/carts/{id}/add-bulk-items
     */
    @PostMapping("/{id}/add-bulk-items")
    @Operation(summary = "Add bulk items", description = "Add multiple items to cart at once")
    public ResponseEntity<List<CartItem>> addBulkItems(
            @PathVariable UUID id,
            @Valid @RequestBody AddBulkItemsRequest request) {
        List<CartService.BulkItemRequest> items = request.getItems().stream()
                .map(item -> new CartService.BulkItemRequest(
                        item.getProductId(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getNotes()
                ))
                .toList();
        List<CartItem> addedItems = cartService.addBulkItems(id, items);
        return ResponseEntity.status(HttpStatus.CREATED).body(addedItems);
    }

    /**
     * Convert cart to lead
     * POST /api/carts/{id}/convert-to-lead
     */
    @PostMapping("/{id}/convert-to-lead")
    @IsBuyer
    @Operation(summary = "Convert cart to lead", description = "Convert shopping cart to a sales lead (requires buyer authentication)")
    public ResponseEntity<Lead> convertToLead(
            @PathVariable UUID id,
            @Valid @RequestBody ConvertToLeadRequest request) {
        Lead lead = cartService.convertToLead(
                id,
                request.getSellerId(),
                request.getBuyerFirstName(),
                request.getBuyerLastName(),
                request.getBuyerEmail(),
                request.getBuyerPhone(),
                request.getBuyerCompanyName()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(lead);
    }

    /**
     * Clone cart
     * POST /api/carts/{id}/clone
     */
    @PostMapping("/{id}/clone")
    @IsBuyer
    @Operation(summary = "Clone cart", description = "Clone cart for reordering (requires buyer authentication)")
    public ResponseEntity<Cart> cloneCart(
            @PathVariable UUID id,
            @Valid @RequestBody(required = false) CloneCartRequest request) {
        UUID buyerId = request != null ? request.getBuyerId() : null;
        UUID customerId = request != null ? request.getCustomerId() : null;
        Cart clonedCart = cartService.cloneCart(id, buyerId, customerId);
        return ResponseEntity.ok(clonedCart);
    }

    /**
     * Merge carts
     * POST /api/carts/{id}/merge
     */
    @PostMapping("/{id}/merge")
    @IsBuyer
    @Operation(summary = "Merge carts", description = "Merge another cart into this one (requires buyer authentication)")
    public ResponseEntity<Cart> mergeCart(
            @PathVariable UUID id,
            @Valid @RequestBody MergeCartRequest request) {
        Cart mergedCart = cartService.mergeCart(id, request.getOtherCartId());
        return ResponseEntity.ok(mergedCart);
    }

    /**
     * Validate cart
     * GET /api/carts/{id}/validate
     */
    @GetMapping("/{id}/validate")
    @Operation(summary = "Validate cart", description = "Check if cart is valid for checkout")
    public ResponseEntity<Map<String, Object>> validateCart(@PathVariable UUID id) {
        Map<String, Object> validation = cartService.validateCart(id);
        boolean isValid = (boolean) validation.get("valid");
        if (isValid) {
            return ResponseEntity.ok(validation);
        } else {
            return ResponseEntity.badRequest().body(validation);
        }
    }

    // ========== Request DTOs ==========

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateCartRequest {
        private UUID buyerId;
        private UUID customerId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateCartRequest {
        private UUID buyerId;
        private UUID customerId;
        private Boolean isActive;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddItemRequest {
        @NotNull(message = "Product ID is required")
        private UUID productId;
        @NotNull(message = "Quantity is required")
        private BigDecimal quantity;
        @NotNull(message = "Unit price is required")
        private BigDecimal unitPrice;
        private String notes;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RemoveItemRequest {
        @NotNull(message = "Item ID is required")
        private UUID itemId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddBulkItemsRequest {
        @NotNull(message = "Items array is required")
        private List<BulkItem> items;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class BulkItem {
            @NotNull
            private UUID productId;
            private BigDecimal quantity;
            private BigDecimal unitPrice;
            private String notes;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ConvertToLeadRequest {
        @NotNull(message = "Seller ID is required")
        private UUID sellerId;
        @NotNull(message = "Buyer first name is required")
        private String buyerFirstName;
        @NotNull(message = "Buyer last name is required")
        private String buyerLastName;
        @NotNull(message = "Buyer email is required")
        private String buyerEmail;
        private String buyerPhone;
        private String buyerCompanyName;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CloneCartRequest {
        private UUID buyerId;
        private UUID customerId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MergeCartRequest {
        @NotNull(message = "Other cart ID is required")
        private UUID otherCartId;
    }
}
