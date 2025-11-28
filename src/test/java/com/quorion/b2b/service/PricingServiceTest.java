package com.quorion.b2b.service;

import com.quorion.b2b.model.commerce.PriceTier;
import com.quorion.b2b.model.product.ListPrice;
import com.quorion.b2b.model.product.ProductSKU;
import com.quorion.b2b.model.tenant.Tenant;
import com.quorion.b2b.model.tenant.TenantAddress;
import com.quorion.b2b.repository.ListPriceRepository;
import com.quorion.b2b.repository.PriceTierRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PricingService
 * Tests all advanced pricing logic: volume, buyer-specific, destination, tier discounts
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PricingService Tests")
class PricingServiceTest {

    @Mock
    private PriceTierRepository priceTierRepository;

    @Mock
    private ListPriceRepository listPriceRepository;

    @InjectMocks
    private PricingService pricingService;

    private UUID skuId;
    private UUID buyerId;
    private UUID sellerId;
    private UUID destinationId;

    @BeforeEach
    void setUp() {
        skuId = UUID.randomUUID();
        buyerId = UUID.randomUUID();
        sellerId = UUID.randomUUID();
        destinationId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Should apply volume-based pricing for quantity tiers")
    void testVolumePricing() {
        // Arrange
        BigDecimal quantity = new BigDecimal("100");
        List<PriceTier> tiers = createVolumeTiers();

        when(priceTierRepository.findByProductSkuId(skuId)).thenReturn(tiers);

        // Act
        BigDecimal price = pricingService.calculatePrice(skuId, quantity, null, null, null);

        // Assert
        assertNotNull(price);
        // Quantity 100 should match tier 3 with min=100, price=6.00
        assertEquals(new BigDecimal("6.00"), price);
        verify(priceTierRepository).findByProductSkuId(skuId);
    }

    @Test
    @DisplayName("Should apply buyer-specific pricing when buyer is provided")
    void testBuyerSpecificPricing() {
        // Arrange
        BigDecimal quantity = new BigDecimal("50");
        List<PriceTier> buyerTiers = createBuyerSpecificTiers(buyerId);

        when(priceTierRepository.findByProductSkuIdAndBuyerId(skuId, buyerId))
                .thenReturn(buyerTiers);

        // Act
        BigDecimal price = pricingService.calculatePrice(skuId, quantity, buyerId, null, null);

        // Assert
        assertNotNull(price);
        // Buyer-specific price should be 7.50
        assertEquals(new BigDecimal("7.50"), price);
        verify(priceTierRepository).findByProductSkuIdAndBuyerId(skuId, buyerId);
    }

    @Test
    @DisplayName("Should apply destination-specific pricing")
    void testDestinationSpecificPricing() {
        // Arrange
        BigDecimal quantity = new BigDecimal("30");
        List<PriceTier> destTiers = createDestinationSpecificTiers(destinationId);

        when(priceTierRepository.findByProductSkuIdAndDestinationId(skuId, destinationId))
                .thenReturn(destTiers);

        // Act
        BigDecimal price = pricingService.calculatePrice(skuId, quantity, null, destinationId, null);

        // Assert
        assertNotNull(price);
        // Destination-specific price should be 9.00
        assertEquals(new BigDecimal("9.00"), price);
        verify(priceTierRepository).findByProductSkuIdAndDestinationId(skuId, destinationId);
    }

    @Test
    @DisplayName("Should prioritize buyer + destination pricing (most specific)")
    void testBuyerDestinationPricingPriority() {
        // Arrange
        BigDecimal quantity = new BigDecimal("50");
        List<PriceTier> combinedTiers = createBuyerDestinationTiers(buyerId, destinationId);

        when(priceTierRepository.findByProductSkuIdAndBuyerIdAndDestinationId(
                skuId, buyerId, destinationId)).thenReturn(combinedTiers);

        // Act
        BigDecimal price = pricingService.calculatePrice(skuId, quantity, buyerId, destinationId, null);

        // Assert
        assertNotNull(price);
        // Most specific price should win: 6.50
        assertEquals(new BigDecimal("6.50"), price);
        verify(priceTierRepository).findByProductSkuIdAndBuyerIdAndDestinationId(
                skuId, buyerId, destinationId);
    }

    @Test
    @DisplayName("Should apply tier discount percentage")
    void testDiscountApplication() {
        // Arrange
        BigDecimal quantity = new BigDecimal("100");
        List<PriceTier> tiersWithDiscount = createTiersWithDiscount();

        when(priceTierRepository.findByProductSkuId(skuId)).thenReturn(tiersWithDiscount);

        // Act
        BigDecimal price = pricingService.calculatePrice(skuId, quantity, null, null, null);

        // Assert
        assertNotNull(price);
        // Base price 10.00 with 20% discount = 8.00
        assertEquals(new BigDecimal("8.00"), price);
    }

    @Test
    @DisplayName("Should filter tiers by date validity")
    void testDateValidityFiltering() {
        // Arrange
        BigDecimal quantity = new BigDecimal("50");
        List<PriceTier> tiersWithDates = createTiersWithDateValidity();

        when(priceTierRepository.findByProductSkuId(skuId)).thenReturn(tiersWithDates);

        // Act
        BigDecimal price = pricingService.calculatePrice(skuId, quantity, null, null, null);

        // Assert
        assertNotNull(price);
        // Only current valid tier should apply: price 7.00
        assertEquals(new BigDecimal("7.00"), price);
    }

    @Test
    @DisplayName("Should fallback to list price when no tiers match")
    void testListPriceFallback() {
        // Arrange
        BigDecimal quantity = new BigDecimal("5"); // Too small for any tier
        List<ListPrice> listPrices = createListPrices();

        when(priceTierRepository.findByProductSkuId(skuId)).thenReturn(new ArrayList<>());
        when(listPriceRepository.findBySkuId(skuId)).thenReturn(listPrices);

        // Act
        BigDecimal price = pricingService.calculatePrice(skuId, quantity, null, null, null);

        // Assert
        assertNotNull(price);
        // Should use list price: 12.00
        assertEquals(new BigDecimal("12.00"), price);
        verify(listPriceRepository).findBySkuId(skuId);
    }

    @Test
    @DisplayName("Should return null when no pricing available")
    void testNoPricingAvailable() {
        // Arrange
        BigDecimal quantity = new BigDecimal("10");

        when(priceTierRepository.findByProductSkuId(skuId)).thenReturn(new ArrayList<>());
        when(listPriceRepository.findBySkuId(skuId)).thenReturn(new ArrayList<>());

        // Act
        BigDecimal price = pricingService.calculatePrice(skuId, quantity, null, null, null);

        // Assert
        assertNull(price);
    }

    @Test
    @DisplayName("Should correctly calculate total price")
    void testTotalPriceCalculation() {
        // Arrange
        BigDecimal quantity = new BigDecimal("100");
        List<PriceTier> tiers = createVolumeTiers();

        when(priceTierRepository.findByProductSkuId(skuId)).thenReturn(tiers);

        // Act
        BigDecimal totalPrice = pricingService.calculateTotalPrice(skuId, quantity, null, null, null);

        // Assert
        assertNotNull(totalPrice);
        // Unit price 6.00 * quantity 100 = 600.00
        assertEquals(new BigDecimal("600.00"), totalPrice);
    }

    @Test
    @DisplayName("Should correctly identify when pricing is available")
    void testHasPricing() {
        // Arrange
        List<PriceTier> tiers = createVolumeTiers();

        when(priceTierRepository.findByProductSkuId(skuId)).thenReturn(tiers);

        // Act
        boolean hasPricing = pricingService.hasPricing(skuId);

        // Assert
        assertTrue(hasPricing);
    }

    // ========== Helper Methods for Test Data ==========

    private List<PriceTier> createVolumeTiers() {
        List<PriceTier> tiers = new ArrayList<>();

        // Tier 1: 1-49 units @ $10.00
        tiers.add(PriceTier.builder()
                
                .minimumUomQuantity(new BigDecimal("1"))
                .maximumUomQuantity(new BigDecimal("49"))
                .pricePerUom(new BigDecimal("10.00"))
                .isActive(true)
                .build());

        // Tier 2: 50-99 units @ $8.00
        tiers.add(PriceTier.builder()
                
                .minimumUomQuantity(new BigDecimal("50"))
                .maximumUomQuantity(new BigDecimal("99"))
                .pricePerUom(new BigDecimal("8.00"))
                .isActive(true)
                .build());

        // Tier 3: 100+ units @ $6.00
        tiers.add(PriceTier.builder()
                
                .minimumUomQuantity(new BigDecimal("100"))
                .pricePerUom(new BigDecimal("6.00"))
                .isActive(true)
                .build());

        return tiers;
    }

    private List<PriceTier> createBuyerSpecificTiers(UUID buyerId) {
        List<PriceTier> tiers = new ArrayList<>();

        Tenant buyer = new Tenant();
        buyer.setId(buyerId);

        tiers.add(PriceTier.builder()
                
                .buyer(buyer)
                .minimumUomQuantity(new BigDecimal("1"))
                .pricePerUom(new BigDecimal("7.50"))
                .isActive(true)
                .build());

        return tiers;
    }

    private List<PriceTier> createDestinationSpecificTiers(UUID destinationId) {
        List<PriceTier> tiers = new ArrayList<>();

        TenantAddress destination = new TenantAddress();
        destination.setId(destinationId);

        tiers.add(PriceTier.builder()
                
                .destination(destination)
                .minimumUomQuantity(new BigDecimal("1"))
                .pricePerUom(new BigDecimal("9.00"))
                .isActive(true)
                .build());

        return tiers;
    }

    private List<PriceTier> createBuyerDestinationTiers(UUID buyerId, UUID destinationId) {
        List<PriceTier> tiers = new ArrayList<>();

        Tenant buyer = new Tenant();
        buyer.setId(buyerId);

        TenantAddress destination = new TenantAddress();
        destination.setId(destinationId);

        tiers.add(PriceTier.builder()
                
                .buyer(buyer)
                .destination(destination)
                .minimumUomQuantity(new BigDecimal("1"))
                .pricePerUom(new BigDecimal("6.50"))
                .isActive(true)
                .build());

        return tiers;
    }

    private List<PriceTier> createTiersWithDiscount() {
        List<PriceTier> tiers = new ArrayList<>();

        tiers.add(PriceTier.builder()
                
                .minimumUomQuantity(new BigDecimal("50"))
                .pricePerUom(new BigDecimal("10.00"))
                .discountPercent(new BigDecimal("20.00"))  // 20% discount
                .isActive(true)
                .build());

        return tiers;
    }

    private List<PriceTier> createTiersWithDateValidity() {
        List<PriceTier> tiers = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        // Expired tier
        tiers.add(PriceTier.builder()
                
                .minimumUomQuantity(new BigDecimal("1"))
                .pricePerUom(new BigDecimal("15.00"))
                .validFromDate(now.minusDays(30))
                .validToDate(now.minusDays(1))  // Expired yesterday
                .isActive(true)
                .build());

        // Current valid tier
        tiers.add(PriceTier.builder()
                
                .minimumUomQuantity(new BigDecimal("1"))
                .pricePerUom(new BigDecimal("7.00"))
                .validFromDate(now.minusDays(1))
                .validToDate(now.plusDays(30))  // Valid now
                .isActive(true)
                .build());

        // Future tier
        tiers.add(PriceTier.builder()
                
                .minimumUomQuantity(new BigDecimal("1"))
                .pricePerUom(new BigDecimal("5.00"))
                .validFromDate(now.plusDays(1))  // Starts tomorrow
                .isActive(true)
                .build());

        return tiers;
    }

    private List<ListPrice> createListPrices() {
        List<ListPrice> prices = new ArrayList<>();

        ProductSKU sku = new ProductSKU();
        sku.setId(skuId);

        prices.add(ListPrice.builder()
                
                .sku(sku)
                .price(new BigDecimal("12.00"))
                .isActive(true)
                .build());

        return prices;
    }
}
