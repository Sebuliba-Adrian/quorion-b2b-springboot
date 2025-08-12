package com.quorion.b2b.service;

import com.quorion.b2b.model.commerce.*;
import com.quorion.b2b.model.product.Product;
import com.quorion.b2b.model.tenant.Tenant;
import com.quorion.b2b.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Cart Service with comprehensive cart management
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final TenantRepository tenantRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final LeadRepository leadRepository;

    /**
     * Get all carts with optional filtering
     */
    @Transactional(readOnly = true)
    public List<Cart> getAllCarts(UUID buyerId, Boolean isActive) {
        List<Cart> carts;
        if (buyerId != null && isActive != null) {
            carts = cartRepository.findByBuyerIdAndIsActive(buyerId, isActive);
        } else if (buyerId != null) {
            carts = cartRepository.findByBuyerId(buyerId);
        } else if (isActive != null) {
            carts = cartRepository.findByIsActive(isActive);
        } else {
            carts = cartRepository.findAll();
        }
        // Initialize lazy collections to prevent LazyInitializationException
        carts.forEach(cart -> {
            if (cart.getItems() != null) {
                cart.getItems().size(); // Force initialization
            }
        });
        return carts;
    }

    /**
     * Get cart by ID
     */
    @Transactional(readOnly = true)
    public Cart getCartById(UUID id) {
        return cartRepository.findById(id)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Cart not found with id: " + id));
    }

    /**
     * Create new cart
     */
    @Transactional
    public Cart createCart(UUID buyerId, UUID customerId) {
        Cart cart = new Cart();

        if (buyerId != null) {
            Tenant buyer = tenantRepository.findById(buyerId)
                    .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Buyer not found"));
            cart.setBuyer(buyer);
        }

        if (customerId != null) {
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Customer not found"));
            cart.setCustomer(customer);
        }

        cart.setIsActive(true);
        return cartRepository.save(cart);
    }

    /**
     * Update cart
     */
    @Transactional
    public Cart updateCart(UUID id, UUID buyerId, UUID customerId, Boolean isActive) {
        Cart cart = getCartById(id);

        if (buyerId != null) {
            Tenant buyer = tenantRepository.findById(buyerId)
                    .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Buyer not found"));
            cart.setBuyer(buyer);
        }

        if (customerId != null) {
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Customer not found"));
            cart.setCustomer(customer);
        }

        if (isActive != null) {
            cart.setIsActive(isActive);
        }

        return cartRepository.save(cart);
    }

    /**
     * Delete cart
     */
    @Transactional
    public void deleteCart(UUID id) {
        Cart cart = getCartById(id);
        cartRepository.delete(cart);
    }

    /**
     * Add or update item in cart
     */
    @Transactional
    public CartItem addItem(UUID cartId, UUID productId, BigDecimal quantity, BigDecimal unitPrice, String notes) {
        Cart cart = getCartById(cartId);

        // Validate product exists
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Product not found"));

        // Validate quantity and price
        if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new jakarta.persistence.EntityNotFoundException("Quantity must be greater than 0");
        }
        if (unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new jakarta.persistence.EntityNotFoundException("Unit price cannot be negative");
        }

        // Check if item already exists in cart (and not soft-deleted)
        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId) && item.getDeletedAt() == null)
                .findFirst();

        if (existingItem.isPresent()) {
            // Update existing item
            CartItem item = existingItem.get();
            item.setQuantity(quantity);
            item.setUnitPrice(unitPrice);
            if (notes != null) {
                item.setNotes(notes);
            }
            return cartItemRepository.save(item);
        } else {
            // Create new item
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            newItem.setUnitPrice(unitPrice);
            newItem.setNotes(notes != null ? notes : "");
            return cartItemRepository.save(newItem);
        }
    }

    /**
     * Remove item from cart (soft delete)
     */
    @Transactional
    public void removeItem(UUID cartId, UUID itemId) {
        Cart cart = getCartById(cartId);

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId) && i.getDeletedAt() == null)
                .findFirst()
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Item not found in cart"));

        // Soft delete
        item.softDelete();
        cartItemRepository.save(item);
    }

    /**
     * Clear all items from cart
     */
    @Transactional
    public void clearCart(UUID cartId) {
        Cart cart = getCartById(cartId);
        cart.clear();
        cartRepository.save(cart);
    }

    /**
     * Add multiple items to cart at once
     */
    @Transactional
    public List<CartItem> addBulkItems(UUID cartId, List<BulkItemRequest> items) {
        Cart cart = getCartById(cartId);
        List<CartItem> addedItems = new ArrayList<>();

        for (BulkItemRequest itemData : items) {
            try {
                // Validate product exists
                Product product = productRepository.findById(itemData.getProductId())
                        .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Product not found: " + itemData.getProductId()));

                // Check if item already exists
                Optional<CartItem> existing = cart.getItems().stream()
                        .filter(item -> item.getProduct().getId().equals(itemData.getProductId()) && item.getDeletedAt() == null)
                        .findFirst();

                if (existing.isPresent()) {
                    // Update quantity
                    CartItem item = existing.get();
                    item.setQuantity(item.getQuantity().add(itemData.getQuantity()));
                    cartItemRepository.save(item);
                    addedItems.add(item);
                } else {
                    // Create new item
                    CartItem newItem = new CartItem();
                    newItem.setCart(cart);
                    newItem.setProduct(product);
                    newItem.setQuantity(itemData.getQuantity());
                    newItem.setUnitPrice(itemData.getUnitPrice());
                    newItem.setNotes(itemData.getNotes() != null ? itemData.getNotes() : "");
                    CartItem saved = cartItemRepository.save(newItem);
                    addedItems.add(saved);
                }
            } catch (Exception e) {
                log.error("Failed to add item to cart: {}", e.getMessage());
            }
        }

        return addedItems;
    }

    /**
     * Convert cart to a lead
     */
    @Transactional
    public Lead convertToLead(UUID cartId, UUID sellerId, String buyerFirstName, String buyerLastName,
                              String buyerEmail, String buyerPhone, String buyerCompanyName) {
        Cart cart = getCartById(cartId);

        // Validate cart has items
        long activeItemsCount = cart.getItems().stream()
                .filter(item -> item.getDeletedAt() == null)
                .count();

        if (activeItemsCount == 0) {
            throw new jakarta.persistence.EntityNotFoundException("Cannot convert empty cart to lead");
        }

        // Validate seller exists
        Tenant seller = tenantRepository.findById(sellerId)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Seller not found"));

        // Create lead
        Lead lead = new Lead();
        lead.setSeller(seller);
        lead.setCart(cart);
        lead.setBuyerFirstName(buyerFirstName);
        lead.setBuyerLastName(buyerLastName);
        lead.setBuyerEmail(buyerEmail);
        lead.setBuyerPhone(buyerPhone != null ? buyerPhone : "");
        lead.setBuyerCompanyName(buyerCompanyName != null ? buyerCompanyName : "");

        // Set initial status
        lead.setStatus(SalesLeadStatus.NEW);

        Lead savedLead = leadRepository.save(lead);

        // Deactivate cart
        cart.setIsActive(false);
        cartRepository.save(cart);

        log.info("Cart {} converted to lead {}", cartId, savedLead.getId());

        return savedLead;
    }

    /**
     * Clone cart for reordering
     */
    @Transactional
    public Cart cloneCart(UUID cartId, UUID buyerId, UUID customerId) {
        Cart originalCart = getCartById(cartId);

        // Create new cart
        Cart newCart = new Cart();
        newCart.setIsActive(true);

        if (buyerId != null) {
            Tenant buyer = tenantRepository.findById(buyerId)
                    .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Buyer not found"));
            newCart.setBuyer(buyer);
        } else if (originalCart.getBuyer() != null) {
            newCart.setBuyer(originalCart.getBuyer());
        }

        if (customerId != null) {
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Customer not found"));
            newCart.setCustomer(customer);
        } else if (originalCart.getCustomer() != null) {
            newCart.setCustomer(originalCart.getCustomer());
        }

        Cart savedCart = cartRepository.save(newCart);

        // Clone items
        for (CartItem originalItem : originalCart.getItems()) {
            if (originalItem.getDeletedAt() == null) {
                CartItem newItem = new CartItem();
                newItem.setCart(savedCart);
                newItem.setProduct(originalItem.getProduct());
                newItem.setQuantity(originalItem.getQuantity());
                newItem.setUnitPrice(originalItem.getUnitPrice());
                newItem.setNotes(originalItem.getNotes());
                cartItemRepository.save(newItem);
            }
        }

        log.info("Cart {} cloned to new cart {}", cartId, savedCart.getId());

        return savedCart;
    }

    /**
     * Merge another cart into this one
     */
    @Transactional
    public Cart mergeCart(UUID cartId, UUID otherCartId) {
        Cart cart = getCartById(cartId);
        Cart otherCart = getCartById(otherCartId);

        // Merge items
        for (CartItem otherItem : otherCart.getItems()) {
            if (otherItem.getDeletedAt() == null) {
                // Check if product already exists in target cart
                Optional<CartItem> existing = cart.getItems().stream()
                        .filter(item -> item.getProduct().getId().equals(otherItem.getProduct().getId())
                                && item.getDeletedAt() == null)
                        .findFirst();

                if (existing.isPresent()) {
                    // Update quantity
                    CartItem item = existing.get();
                    item.setQuantity(item.getQuantity().add(otherItem.getQuantity()));
                    cartItemRepository.save(item);
                } else {
                    // Create new item
                    CartItem newItem = new CartItem();
                    newItem.setCart(cart);
                    newItem.setProduct(otherItem.getProduct());
                    newItem.setQuantity(otherItem.getQuantity());
                    newItem.setUnitPrice(otherItem.getUnitPrice());
                    newItem.setNotes(otherItem.getNotes());
                    cartItemRepository.save(newItem);
                }
            }
        }

        // Deactivate the merged cart
        otherCart.setIsActive(false);
        cartRepository.save(otherCart);

        log.info("Cart {} merged into cart {}", otherCartId, cartId);

        return cart;
    }

    /**
     * Validate cart
     */
    @Transactional(readOnly = true)
    public Map<String, Object> validateCart(UUID cartId) {
        Cart cart = getCartById(cartId);
        List<String> errors = new ArrayList<>();

        // Check if cart has items
        long activeItemsCount = cart.getItems().stream()
                .filter(item -> item.getDeletedAt() == null)
                .count();

        if (activeItemsCount == 0) {
            errors.add("Cart is empty");
        }

        // Validate each item
        for (CartItem item : cart.getItems()) {
            if (item.getDeletedAt() == null) {
                if (item.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
                    errors.add("Item " + item.getId() + " has invalid quantity");
                }
                if (item.getUnitPrice().compareTo(BigDecimal.ZERO) < 0) {
                    errors.add("Item " + item.getId() + " has invalid unit price");
                }
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("valid", errors.isEmpty());
        result.put("errors", errors);

        return result;
    }

    /**
     * Bulk item request DTO
     */
    public static class BulkItemRequest {
        private UUID productId;
        private BigDecimal quantity;
        private BigDecimal unitPrice;
        private String notes;

        public BulkItemRequest() {}

        public BulkItemRequest(UUID productId, BigDecimal quantity, BigDecimal unitPrice, String notes) {
            this.productId = productId;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.notes = notes;
        }

        public UUID getProductId() { return productId; }
        public void setProductId(UUID productId) { this.productId = productId; }

        public BigDecimal getQuantity() { return quantity; }
        public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

        public BigDecimal getUnitPrice() { return unitPrice; }
        public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }
}
