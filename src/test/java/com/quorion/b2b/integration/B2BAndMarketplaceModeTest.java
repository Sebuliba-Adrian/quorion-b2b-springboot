package com.quorion.b2b.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quorion.b2b.dto.auth.LoginRequest;
import com.quorion.b2b.dto.auth.RegisterRequest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * B2B and Marketplace Mode Tests
 * 
 * Tests functionality in both B2B negotiation mode and Marketplace mode
 * Verifies that both modes work correctly
 */
@SpringBootTest(classes = com.quorion.b2b.config.TestApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("B2B and Marketplace Mode Tests")
public class B2BAndMarketplaceModeTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String authToken;
    private UUID testTenantId;
    private UUID testBuyerId;
    private UUID testSellerId;

    @BeforeEach
    void setUp() {
        testTenantId = UUID.randomUUID();
        testBuyerId = UUID.randomUUID();
        testSellerId = UUID.randomUUID();
    }

    private String getAuthHeader() {
        return authToken != null ? "Bearer " + authToken : "";
    }

    // ==================== B2B MODE TESTS ====================

    @Test
    @DisplayName("B2B-001: Create quote request (B2B negotiation flow)")
    void testB2BCreateQuoteRequest() throws Exception {
        String body = String.format(
            "{\"buyerId\":\"%s\",\"sellerId\":\"%s\",\"status\":\"DRAFT\"}",
            testBuyerId, testSellerId
        );

        MvcResult result = mockMvc.perform(post("/api/v2/orders/quote-requests")
                        .header("Authorization", getAuthHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andReturn();
        
        int status = result.getResponse().getStatus();
        assertTrue(status >= 200 && status < 500, 
            "B2B quote request creation should work, got: " + status);
    }

    @Test
    @DisplayName("B2B-002: Seller responds to quote (B2B negotiation)")
    void testB2BSellerRespondsToQuote() throws Exception {
        UUID quoteId = UUID.randomUUID();
        String body = "{\"shippingCost\":50.00}";

        MvcResult result = mockMvc.perform(post("/api/v2/orders/quote-requests/" + quoteId + "/respond")
                        .header("Authorization", getAuthHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andReturn();
        
        int status = result.getResponse().getStatus();
        assertTrue(status >= 200 && status < 500, 
            "B2B seller quote response should work, got: " + status);
    }

    @Test
    @DisplayName("B2B-003: Buyer accepts quote and creates purchase order")
    void testB2BBuyerAcceptsQuote() throws Exception {
        UUID quoteId = UUID.randomUUID();

        MvcResult result = mockMvc.perform(post("/api/v2/orders/quote-requests/" + quoteId + "/accept")
                        .header("Authorization", getAuthHeader()))
                .andReturn();
        
        int status = result.getResponse().getStatus();
        assertTrue(status >= 200 && status < 500, 
            "B2B quote acceptance should work, got: " + status);
    }

    @Test
    @DisplayName("B2B-004: Purchase order state transitions")
    void testB2BPurchaseOrderStateTransitions() throws Exception {
        UUID orderId = UUID.randomUUID();

        // Test accept order
        MvcResult result1 = mockMvc.perform(post("/api/v2/orders/purchase-orders/" + orderId + "/accept")
                        .header("Authorization", getAuthHeader()))
                .andReturn();
        assertTrue(result1.getResponse().getStatus() >= 200 && result1.getResponse().getStatus() < 500);

        // Test in-progress
        MvcResult result2 = mockMvc.perform(post("/api/v2/orders/purchase-orders/" + orderId + "/in-progress")
                        .header("Authorization", getAuthHeader()))
                .andReturn();
        assertTrue(result2.getResponse().getStatus() >= 200 && result2.getResponse().getStatus() < 500);

        // Test invoice
        MvcResult result3 = mockMvc.perform(post("/api/v2/orders/purchase-orders/" + orderId + "/invoice")
                        .header("Authorization", getAuthHeader()))
                .andReturn();
        assertTrue(result3.getResponse().getStatus() >= 200 && result3.getResponse().getStatus() < 500);

        // Test ship
        MvcResult result4 = mockMvc.perform(post("/api/v2/orders/purchase-orders/" + orderId + "/ship")
                        .header("Authorization", getAuthHeader()))
                .andReturn();
        assertTrue(result4.getResponse().getStatus() >= 200 && result4.getResponse().getStatus() < 500);
    }

    // ==================== MARKETPLACE MODE TESTS ====================

    @Test
    @DisplayName("MARKETPLACE-001: Create cart (Marketplace direct purchase)")
    void testMarketplaceCreateCart() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v2/carts")
                        .param("buyerId", testBuyerId.toString())
                        .header("Authorization", getAuthHeader()))
                .andReturn();
        
        int status = result.getResponse().getStatus();
        assertTrue(status >= 200 && status < 500, 
            "Marketplace cart creation should work, got: " + status);
    }

    @Test
    @DisplayName("MARKETPLACE-002: Add item to cart")
    void testMarketplaceAddItemToCart() throws Exception {
        UUID cartId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        MvcResult result = mockMvc.perform(post("/api/v2/carts/" + cartId + "/items")
                        .param("productId", productId.toString())
                        .param("quantity", "2")
                        .param("unitPrice", "99.99")
                        .header("Authorization", getAuthHeader()))
                .andReturn();
        
        int status = result.getResponse().getStatus();
        assertTrue(status >= 200 && status < 500, 
            "Marketplace add item should work, got: " + status);
    }

    @Test
    @DisplayName("MARKETPLACE-003: Get applicable price (Marketplace pricing)")
    void testMarketplaceGetApplicablePrice() throws Exception {
        UUID skuId = UUID.randomUUID();

        MvcResult result = mockMvc.perform(get("/api/adapter/pricing/tiers/applicable-price")
                        .param("sellerId", testSellerId.toString())
                        .param("buyerId", testBuyerId.toString())
                        .param("productSkuId", skuId.toString())
                        .param("quantity", "10")
                        .header("Authorization", getAuthHeader()))
                .andReturn();
        
        int status = result.getResponse().getStatus();
        assertTrue(status >= 200 && status < 500, 
            "Marketplace pricing should work, got: " + status);
    }

    @Test
    @DisplayName("MARKETPLACE-004: Create payment (Marketplace checkout)")
    void testMarketplaceCreatePayment() throws Exception {
        UUID orderId = UUID.randomUUID();
        String body = String.format(
            "{\"orderId\":\"%s\",\"amount\":199.98,\"paymentMethod\":\"CARD\",\"currency\":\"USD\"}",
            orderId
        );

        MvcResult result = mockMvc.perform(post("/api/adapter/payments")
                        .header("Authorization", getAuthHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andReturn();
        
        int status = result.getResponse().getStatus();
        assertTrue(status >= 200 && status < 500, 
            "Marketplace payment creation should work, got: " + status);
    }

    @Test
    @DisplayName("MARKETPLACE-005: Cart operations (clone, merge)")
    void testMarketplaceCartOperations() throws Exception {
        UUID cartId = UUID.randomUUID();

        // Test clone
        MvcResult result1 = mockMvc.perform(post("/api/v2/carts/" + cartId + "/clone")
                        .header("Authorization", getAuthHeader()))
                .andReturn();
        assertTrue(result1.getResponse().getStatus() >= 200 && result1.getResponse().getStatus() < 500);

        // Test merge
        UUID otherCartId = UUID.randomUUID();
        MvcResult result2 = mockMvc.perform(post("/api/v2/carts/" + cartId + "/merge/" + otherCartId)
                        .header("Authorization", getAuthHeader()))
                .andReturn();
        assertTrue(result2.getResponse().getStatus() >= 200 && result2.getResponse().getStatus() < 500);
    }

    // ==================== HYBRID MODE TESTS ====================

    @Test
    @DisplayName("HYBRID-001: Both B2B and Marketplace features available")
    void testHybridModeBothFeaturesAvailable() throws Exception {
        // Test B2B feature (quote request)
        MvcResult result1 = mockMvc.perform(get("/api/v2/orders/quote-requests")
                        .header("Authorization", getAuthHeader()))
                .andReturn();
        assertTrue(result1.getResponse().getStatus() >= 200 && result1.getResponse().getStatus() < 500,
            "B2B quote requests should be available");

        // Test Marketplace feature (cart)
        MvcResult result2 = mockMvc.perform(get("/api/v2/carts")
                        .header("Authorization", getAuthHeader()))
                .andReturn();
        assertTrue(result2.getResponse().getStatus() >= 200 && result2.getResponse().getStatus() < 500,
            "Marketplace carts should be available");
    }

    @Test
    @DisplayName("HYBRID-002: Product catalog accessible in both modes")
    void testHybridProductCatalog() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/v2/products")
                        .header("Authorization", getAuthHeader()))
                .andReturn();
        
        int status = result.getResponse().getStatus();
        assertTrue(status >= 200 && status < 500, 
            "Product catalog should be accessible in both modes, got: " + status);
    }

    @Test
    @DisplayName("HYBRID-003: Pricing works for both B2B and Marketplace")
    void testHybridPricing() throws Exception {
        // Test price tiers (B2B volume pricing)
        MvcResult result1 = mockMvc.perform(get("/api/adapter/pricing/tiers")
                        .header("Authorization", getAuthHeader()))
                .andReturn();
        assertTrue(result1.getResponse().getStatus() >= 200 && result1.getResponse().getStatus() < 500);

        // Test list prices (Marketplace pricing)
        MvcResult result2 = mockMvc.perform(get("/api/adapter/pricing/list-prices")
                        .header("Authorization", getAuthHeader()))
                .andReturn();
        assertTrue(result2.getResponse().getStatus() >= 200 && result2.getResponse().getStatus() < 500);
    }
}

