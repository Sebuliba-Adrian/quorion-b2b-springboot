package com.quorion.b2b.service;

import com.quorion.b2b.model.commerce.PriceTier;
import com.quorion.b2b.model.product.ListPrice;
import com.quorion.b2b.model.product.ProductSKU;
import com.quorion.b2b.model.tenant.Tenant;
import com.quorion.b2b.model.tenant.TenantAddress;
import com.quorion.b2b.repository.ListPriceRepository;
import com.quorion.b2b.repository.PriceTierRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Advanced Pricing Service
 *
 * Implements sophisticated pricing logic:
 * - Volume-based pricing (quantity tiers)
 * - Buyer-specific pricing
 * - Destination-based pricing
 * - Payment/delivery term discounts
 * - Time-based validity
 * - Fallback to list prices
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PricingService {

    private final PriceTierRepository priceTierRepository;
    private final ListPriceRepository listPriceRepository;

    /**
     * Calculate best price for a product SKU
     *
     * Priority order:
     * 1. Buyer + Destination + Quantity specific tier
     * 2. Buyer + Quantity specific tier
     * 3. Destination + Quantity specific tier
     * 4. Quantity-only tier (volume pricing)
     * 5. List price for SKU
     *
     * @param skuId Product SKU ID
     * @param quantity Quantity being purchased
     * @param buyerId Buyer tenant ID (optional)
     * @param destinationId Destination address ID (optional)
     * @param sellerId Seller tenant ID (optional)
     * @return Calculated price per unit, or null if no pricing found
     */
    @Transactional(readOnly = true)
    public BigDecimal calculatePrice(UUID skuId, BigDecimal quantity, UUID buyerId,
                                     UUID destinationId, UUID sellerId) {
        log.debug("Calculating price for SKU {} qty {} buyer {} dest {} seller {}",
                  skuId, quantity, buyerId, destinationId, sellerId);

        // Try buyer + destination + quantity specific pricing first (most specific)
        if (buyerId != null && destinationId != null) {
            BigDecimal price = getBuyerDestinationPrice(skuId, quantity, buyerId, destinationId, sellerId);
            if (price != null) {
                log.debug("Found buyer+destination specific price: {}", price);
                return price;
            }
        }

        // Try buyer + quantity specific pricing
        if (buyerId != null) {
            BigDecimal price = getBuyerSpecificPrice(skuId, quantity, buyerId, sellerId);
            if (price != null) {
                log.debug("Found buyer-specific price: {}", price);
                return price;
            }
        }

        // Try destination + quantity specific pricing
        if (destinationId != null) {
            BigDecimal price = getDestinationSpecificPrice(skuId, quantity, destinationId, sellerId);
            if (price != null) {
                log.debug("Found destination-specific price: {}", price);
                return price;
            }
        }

        // Try volume-based pricing (quantity tiers only)
        BigDecimal volumePrice = getVolumeBasedPrice(skuId, quantity, sellerId);
        if (volumePrice != null) {
            log.debug("Found volume-based price: {}", volumePrice);
            return volumePrice;
        }

        // Fallback to list price
        BigDecimal listPrice = getListPrice(skuId);
        log.debug("Using list price: {}", listPrice);
        return listPrice;
    }

    /**
     * Get buyer and destination specific pricing
     */
    private BigDecimal getBuyerDestinationPrice(UUID skuId, BigDecimal quantity,
                                                 UUID buyerId, UUID destinationId, UUID sellerId) {
        List<PriceTier> tiers = priceTierRepository.findByProductSkuIdAndBuyerIdAndDestinationId(
                skuId, buyerId, destinationId);

        if (sellerId != null) {
            tiers = tiers.stream()
                    .filter(t -> t.getSeller() != null && t.getSeller().getId().equals(sellerId))
                    .toList();
        }

        return findBestTierPrice(tiers, quantity);
    }

    /**
     * Get buyer-specific pricing (any destination)
     */
    private BigDecimal getBuyerSpecificPrice(UUID skuId, BigDecimal quantity, UUID buyerId, UUID sellerId) {
        List<PriceTier> tiers = priceTierRepository.findByProductSkuIdAndBuyerId(skuId, buyerId);

        if (sellerId != null) {
            tiers = tiers.stream()
                    .filter(t -> t.getSeller() != null && t.getSeller().getId().equals(sellerId))
                    .toList();
        }

        return findBestTierPrice(tiers, quantity);
    }

    /**
     * Get destination-specific pricing (any buyer)
     */
    private BigDecimal getDestinationSpecificPrice(UUID skuId, BigDecimal quantity,
                                                    UUID destinationId, UUID sellerId) {
        List<PriceTier> tiers = priceTierRepository.findByProductSkuIdAndDestinationId(skuId, destinationId);

        if (sellerId != null) {
            tiers = tiers.stream()
                    .filter(t -> t.getSeller() != null && t.getSeller().getId().equals(sellerId))
                    .toList();
        }

        return findBestTierPrice(tiers, quantity);
    }

    /**
     * Get volume-based pricing (quantity tiers only, no buyer/destination specific)
     */
    private BigDecimal getVolumeBasedPrice(UUID skuId, BigDecimal quantity, UUID sellerId) {
        List<PriceTier> tiers = priceTierRepository.findByProductSkuId(skuId);

        // Filter to only general tiers (no specific buyer or destination)
        tiers = tiers.stream()
                .filter(t -> t.getBuyer() == null && t.getDestination() == null)
                .filter(t -> sellerId == null || (t.getSeller() != null && t.getSeller().getId().equals(sellerId)))
                .toList();

        return findBestTierPrice(tiers, quantity);
    }

    /**
     * Find best price from applicable tiers
     *
     * Filters by:
     * - Active tiers
     * - Quantity within tier range
     * - Valid date range
     *
     * Returns lowest price among matching tiers
     */
    private BigDecimal findBestTierPrice(List<PriceTier> tiers, BigDecimal quantity) {
        if (tiers == null || tiers.isEmpty()) {
            return null;
        }

        LocalDateTime now = LocalDateTime.now();

        Optional<BigDecimal> bestPrice = tiers.stream()
                .filter(tier -> tier.getIsActive())
                .filter(tier -> isQuantityInRange(tier, quantity))
                .filter(tier -> isDateValid(tier, now))
                .map(tier -> applyDiscount(tier.getPricePerUom(), tier.getDiscountPercent()))
                .min(BigDecimal::compareTo);

        return bestPrice.orElse(null);
    }

    /**
     * Check if quantity is within tier range
     */
    private boolean isQuantityInRange(PriceTier tier, BigDecimal quantity) {
        if (quantity.compareTo(tier.getMinimumUomQuantity()) < 0) {
            return false;
        }

        if (tier.getMaximumUomQuantity() != null &&
            quantity.compareTo(tier.getMaximumUomQuantity()) > 0) {
            return false;
        }

        return true;
    }

    /**
     * Check if tier is valid for current date
     */
    private boolean isDateValid(PriceTier tier, LocalDateTime now) {
        if (tier.getValidFromDate() != null && now.isBefore(tier.getValidFromDate())) {
            return false;
        }

        if (tier.getValidToDate() != null && now.isAfter(tier.getValidToDate())) {
            return false;
        }

        return true;
    }

    /**
     * Apply discount percentage to base price
     */
    private BigDecimal applyDiscount(BigDecimal basePrice, BigDecimal discountPercent) {
        if (discountPercent == null || discountPercent.compareTo(BigDecimal.ZERO) == 0) {
            return basePrice;
        }

        BigDecimal discountMultiplier = BigDecimal.ONE
                .subtract(discountPercent.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP));

        return basePrice.multiply(discountMultiplier).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Get current list price for SKU
     *
     * Returns active list price valid for current date
     */
    @Transactional(readOnly = true)
    public BigDecimal getListPrice(UUID skuId) {
        LocalDateTime now = LocalDateTime.now();

        List<ListPrice> listPrices = listPriceRepository.findBySkuId(skuId);

        Optional<BigDecimal> price = listPrices.stream()
                .filter(lp -> lp.getIsActive())
                .filter(lp -> lp.getStartDate() == null || !lp.getStartDate().isAfter(now))
                .filter(lp -> lp.getEndDate() == null || !lp.getEndDate().isBefore(now))
                .map(ListPrice::getPrice)
                .min(BigDecimal::compareTo);  // Take lowest if multiple

        return price.orElse(null);
    }

    /**
     * Calculate total price for quantity
     */
    public BigDecimal calculateTotalPrice(UUID skuId, BigDecimal quantity, UUID buyerId,
                                          UUID destinationId, UUID sellerId) {
        BigDecimal unitPrice = calculatePrice(skuId, quantity, buyerId, destinationId, sellerId);

        if (unitPrice == null) {
            return null;
        }

        return unitPrice.multiply(quantity).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Get all applicable price tiers for a SKU
     *
     * Useful for displaying pricing breakpoints to buyers
     */
    @Transactional(readOnly = true)
    public List<PriceTier> getApplicableTiers(UUID skuId, UUID buyerId,
                                               UUID destinationId, UUID sellerId) {
        List<PriceTier> allTiers = priceTierRepository.findByProductSkuId(skuId);

        LocalDateTime now = LocalDateTime.now();

        return allTiers.stream()
                .filter(tier -> tier.getIsActive())
                .filter(tier -> isDateValid(tier, now))
                .filter(tier -> matchesContext(tier, buyerId, destinationId, sellerId))
                .sorted(Comparator.comparing(PriceTier::getMinimumUomQuantity))
                .toList();
    }

    /**
     * Check if tier matches the given context (buyer/destination/seller)
     */
    private boolean matchesContext(PriceTier tier, UUID buyerId, UUID destinationId, UUID sellerId) {
        // Check seller match
        if (sellerId != null && tier.getSeller() != null) {
            if (!tier.getSeller().getId().equals(sellerId)) {
                return false;
            }
        }

        // Check buyer match (tier.buyer null means applies to all buyers)
        if (tier.getBuyer() != null && buyerId != null) {
            if (!tier.getBuyer().getId().equals(buyerId)) {
                return false;
            }
        }

        // Check destination match (tier.destination null means applies to all destinations)
        if (tier.getDestination() != null && destinationId != null) {
            if (!tier.getDestination().getId().equals(destinationId)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Validate if pricing is available for a product
     */
    @Transactional(readOnly = true)
    public boolean hasPricing(UUID skuId) {
        // Check if has list price
        BigDecimal listPrice = getListPrice(skuId);
        if (listPrice != null) {
            return true;
        }

        // Check if has any active price tiers
        List<PriceTier> tiers = priceTierRepository.findByProductSkuId(skuId);
        return tiers.stream().anyMatch(PriceTier::getIsActive);
    }
}
