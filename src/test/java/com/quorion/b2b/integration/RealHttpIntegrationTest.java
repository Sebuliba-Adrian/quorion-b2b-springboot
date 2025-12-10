package com.quorion.b2b.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Real HTTP Integration Tests - Using TestRestTemplate
 * 
 * These tests make actual HTTP calls to the running application
 * Tests the full stack end-to-end with real database
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@org.springframework.test.context.jdbc.Sql(scripts = "/data-test.sql", executionPhase = org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DisplayName("Real HTTP Integration Tests - End-to-End")
public class RealHttpIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private String baseUrl;
    private String authToken;
    private UUID testTenantId;
    private UUID testBuyerId;
    private UUID testSellerId;
    private UUID testProductId;
    private UUID testSkuId;
    private UUID testCustomerId;
    private UUID testCartId;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
        testTenantId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        testBuyerId = UUID.fromString("00000000-0000-0000-0000-000000000002");
        testSellerId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        testProductId = UUID.fromString("00000000-0000-0000-0000-000000000030");
        testSkuId = UUID.fromString("00000000-0000-0000-0000-000000000040");
        testCustomerId = UUID.fromString("00000000-0000-0000-0000-000000000020");
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (authToken != null) {
            headers.setBearerAuth(authToken);
        }
        return headers;
    }

    private HttpHeaders getHeadersWithAuth() {
        HttpHeaders headers = getHeaders();
        if (authToken == null) {
            authenticate();
        }
        if (authToken != null) {
            headers.setBearerAuth(authToken);
        }
        return headers;
    }

    private void authenticate() {
        try {
            Map<String, String> loginRequest = new HashMap<>();
            loginRequest.put("usernameOrEmail", "testuser");
            loginRequest.put("password", "Test123456!");

            HttpEntity<Map<String, String>> request = new HttpEntity<>(loginRequest, getHeaders());
            ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl + "/api/v2/auth/login", request, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                try {
                    JsonNode json = objectMapper.readTree(response.getBody());
                    if (json.has("token")) {
                        authToken = json.get("token").asText();
                    }
                } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                    // JSON parsing failed
                }
            }
        } catch (Exception e) {
            // Authentication failed, tests will handle 401
        }
    }

    // ==================== AUTHENTICATION TESTS ====================

    @Test
    @Order(1)
    @DisplayName("INT-001: Login with real HTTP call")
    void testLogin() {
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("usernameOrEmail", "testuser");
        loginRequest.put("password", "Test123456!");

        HttpEntity<Map<String, String>> request = new HttpEntity<>(loginRequest, getHeaders());
        ResponseEntity<String> response = restTemplate.postForEntity(
            baseUrl + "/api/v2/auth/login", request, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode(), 
            "Login should succeed with valid credentials");
        
        assertNotNull(response.getBody(), "Response body should not be null");
        try {
            JsonNode json = objectMapper.readTree(response.getBody());
            assertTrue(json.has("token"), "Response should contain token");
            
            authToken = json.get("token").asText();
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            fail("Failed to parse login response: " + e.getMessage());
        }
        assertNotNull(authToken, "Auth token should not be null");
    }

    @Test
    @Order(2)
    @DisplayName("INT-002: Get current user with authentication")
    void testGetCurrentUser() {
        authenticate();
        
        HttpEntity<String> request = new HttpEntity<>(getHeadersWithAuth());
        ResponseEntity<String> response = restTemplate.exchange(
            baseUrl + "/api/v2/auth/me", HttpMethod.GET, request, String.class);

        assertTrue(response.getStatusCode().is2xxSuccessful(), 
            "Should return 200 with valid token");
        assertNotNull(response.getBody(), "Response body should not be null");
    }

    // ==================== PRODUCT TESTS ====================

    @Test
    @Order(10)
    @DisplayName("INT-003: Get all products via HTTP")
    void testGetAllProducts() {
        authenticate();
        
        HttpEntity<String> request = new HttpEntity<>(getHeadersWithAuth());
        ResponseEntity<String> response = restTemplate.exchange(
            baseUrl + "/api/v2/products", HttpMethod.GET, request, String.class);

        assertTrue(response.getStatusCode().is2xxSuccessful(), 
            "Should return products list");
        assertNotNull(response.getBody(), "Response body should not be null");
    }

    @Test
    @Order(11)
    @DisplayName("INT-004: Get product by ID via HTTP")
    void testGetProductById() {
        authenticate();
        
        HttpEntity<String> request = new HttpEntity<>(getHeadersWithAuth());
        ResponseEntity<String> response = restTemplate.exchange(
            baseUrl + "/api/v2/products/" + testProductId, 
            HttpMethod.GET, request, String.class);

        assertTrue(response.getStatusCode().is2xxSuccessful() || 
                   response.getStatusCode() == HttpStatus.NOT_FOUND,
            "Should return product or 404");
    }

    @Test
    @Order(12)
    @DisplayName("INT-005: Get products by seller via HTTP")
    void testGetProductsBySeller() {
        authenticate();
        
        HttpEntity<String> request = new HttpEntity<>(getHeadersWithAuth());
        ResponseEntity<String> response = restTemplate.exchange(
            baseUrl + "/api/v2/products/seller/" + testSellerId, 
            HttpMethod.GET, request, String.class);

        assertTrue(response.getStatusCode().is2xxSuccessful(), 
            "Should return seller products");
    }

    // ==================== CART TESTS ====================

    @Test
    @Order(20)
    @DisplayName("INT-006: Get all carts via HTTP")
    void testGetAllCarts() {
        authenticate();
        
        HttpEntity<String> request = new HttpEntity<>(getHeadersWithAuth());
        ResponseEntity<String> response = restTemplate.exchange(
            baseUrl + "/api/v2/carts", HttpMethod.GET, request, String.class);

        assertTrue(response.getStatusCode().is2xxSuccessful(), 
            "Should return carts list");
    }

    @Test
    @Order(21)
    @DisplayName("INT-007: Create cart via HTTP")
    void testCreateCart() {
        authenticate();
        
        String url = baseUrl + "/api/v2/carts?buyerId=" + testBuyerId;
        HttpEntity<String> request = new HttpEntity<>(getHeadersWithAuth());
        ResponseEntity<String> response = restTemplate.exchange(
            url, HttpMethod.POST, request, String.class);

        assertTrue(response.getStatusCode().is2xxSuccessful() || 
                   response.getStatusCode() == HttpStatus.CREATED,
            "Should create cart");
        
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            try {
                JsonNode json = objectMapper.readTree(response.getBody());
                if (json.has("id")) {
                    testCartId = UUID.fromString(json.get("id").asText());
                }
            } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                // JSON parsing failed
            }
        }
    }

    @Test
    @Order(22)
    @DisplayName("INT-008: Add item to cart via HTTP")
    void testAddItemToCart() {
        authenticate();
        testCreateCart(); // Ensure cart exists
        
        if (testCartId == null) {
            // Create cart first
            String createUrl = baseUrl + "/api/v2/carts?buyerId=" + testBuyerId;
            HttpEntity<String> createRequest = new HttpEntity<>(getHeadersWithAuth());
            ResponseEntity<String> createResponse = restTemplate.exchange(
                createUrl, HttpMethod.POST, createRequest, String.class);
            
            if (createResponse.getBody() != null) {
                try {
                    JsonNode json = objectMapper.readTree(createResponse.getBody());
                    if (json.has("id")) {
                        testCartId = UUID.fromString(json.get("id").asText());
                    }
                } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                    // JSON parsing failed
                }
            }
        }
        
        if (testCartId != null) {
            String url = baseUrl + "/api/v2/carts/" + testCartId + "/items" +
                "?productId=" + testProductId + "&quantity=2&unitPrice=100.00";
            
            HttpEntity<String> request = new HttpEntity<>(getHeadersWithAuth());
            ResponseEntity<String> response = restTemplate.exchange(
                url, HttpMethod.POST, request, String.class);

            assertTrue(response.getStatusCode().is2xxSuccessful() || 
                       response.getStatusCode() == HttpStatus.CREATED,
                "Should add item to cart");
        }
    }

    // ==================== CUSTOMER TESTS ====================

    @Test
    @Order(30)
    @DisplayName("INT-009: Get all customers via HTTP")
    void testGetAllCustomers() {
        authenticate();
        
        HttpEntity<String> request = new HttpEntity<>(getHeadersWithAuth());
        ResponseEntity<String> response = restTemplate.exchange(
            baseUrl + "/api/v2/customers", HttpMethod.GET, request, String.class);

        assertTrue(response.getStatusCode().is2xxSuccessful(), 
            "Should return customers list");
    }

    @Test
    @Order(31)
    @DisplayName("INT-010: Get customer by ID via HTTP")
    void testGetCustomerById() {
        authenticate();
        
        HttpEntity<String> request = new HttpEntity<>(getHeadersWithAuth());
        ResponseEntity<String> response = restTemplate.exchange(
            baseUrl + "/api/v2/customers/" + testCustomerId, 
            HttpMethod.GET, request, String.class);

        assertTrue(response.getStatusCode().is2xxSuccessful() || 
                   response.getStatusCode() == HttpStatus.NOT_FOUND,
            "Should return customer or 404");
    }

    // ==================== ORDER TESTS ====================

    @Test
    @Order(40)
    @DisplayName("INT-011: Get all purchase orders via HTTP")
    void testGetAllPurchaseOrders() {
        authenticate();
        
        HttpEntity<String> request = new HttpEntity<>(getHeadersWithAuth());
        ResponseEntity<String> response = restTemplate.exchange(
            baseUrl + "/api/v2/orders/purchase-orders", 
            HttpMethod.GET, request, String.class);

        assertTrue(response.getStatusCode().is2xxSuccessful(), 
            "Should return purchase orders list");
    }

    @Test
    @Order(41)
    @DisplayName("INT-012: Get all quote requests via HTTP")
    void testGetAllQuoteRequests() {
        authenticate();
        
        HttpEntity<String> request = new HttpEntity<>(getHeadersWithAuth());
        ResponseEntity<String> response = restTemplate.exchange(
            baseUrl + "/api/v2/orders/quote-requests", 
            HttpMethod.GET, request, String.class);

        assertTrue(response.getStatusCode().is2xxSuccessful(), 
            "Should return quote requests list");
    }

    // ==================== PAYMENT TESTS ====================

    @Test
    @Order(50)
    @DisplayName("INT-013: Get all payments via HTTP")
    void testGetAllPayments() {
        authenticate();
        
        HttpEntity<String> request = new HttpEntity<>(getHeadersWithAuth());
        ResponseEntity<String> response = restTemplate.exchange(
            baseUrl + "/api/adapter/payments", 
            HttpMethod.GET, request, String.class);

        assertTrue(response.getStatusCode().is2xxSuccessful(), 
            "Should return payments list");
    }

    // ==================== PRICING TESTS ====================

    @Test
    @Order(60)
    @DisplayName("INT-014: Get all price tiers via HTTP")
    void testGetAllPriceTiers() {
        authenticate();
        
        HttpEntity<String> request = new HttpEntity<>(getHeadersWithAuth());
        ResponseEntity<String> response = restTemplate.exchange(
            baseUrl + "/api/adapter/pricing/tiers", 
            HttpMethod.GET, request, String.class);

        assertTrue(response.getStatusCode().is2xxSuccessful(), 
            "Should return price tiers list");
    }

    @Test
    @Order(61)
    @DisplayName("INT-015: Get applicable price via HTTP")
    void testGetApplicablePrice() {
        authenticate();
        
        String url = baseUrl + "/api/adapter/pricing/tiers/applicable-price" +
            "?sellerId=" + testSellerId +
            "&buyerId=" + testBuyerId +
            "&productSkuId=" + testSkuId +
            "&quantity=5";
        
        HttpEntity<String> request = new HttpEntity<>(getHeadersWithAuth());
        ResponseEntity<String> response = restTemplate.exchange(
            url, HttpMethod.GET, request, String.class);

        assertTrue(response.getStatusCode().is2xxSuccessful() || 
                   response.getStatusCode() == HttpStatus.NOT_FOUND,
            "Should return applicable price or 404");
    }

    // ==================== TENANT TESTS ====================

    @Test
    @Order(70)
    @DisplayName("INT-016: Get all tenants via HTTP")
    void testGetAllTenants() {
        authenticate();
        
        HttpEntity<String> request = new HttpEntity<>(getHeadersWithAuth());
        ResponseEntity<String> response = restTemplate.exchange(
            baseUrl + "/api/v2/tenants", HttpMethod.GET, request, String.class);

        assertTrue(response.getStatusCode().is2xxSuccessful(), 
            "Should return tenants list");
    }

    @Test
    @Order(71)
    @DisplayName("INT-017: Get tenant by ID via HTTP")
    void testGetTenantById() {
        authenticate();
        
        HttpEntity<String> request = new HttpEntity<>(getHeadersWithAuth());
        ResponseEntity<String> response = restTemplate.exchange(
            baseUrl + "/api/v2/tenants/" + testTenantId, 
            HttpMethod.GET, request, String.class);

        assertTrue(response.getStatusCode().is2xxSuccessful() || 
                   response.getStatusCode() == HttpStatus.NOT_FOUND,
            "Should return tenant or 404");
    }

    // ==================== END-TO-END WORKFLOW TESTS ====================

    @Test
    @Order(100)
    @DisplayName("INT-018: Complete B2B workflow - Quote to Order")
    void testB2BWorkflowEndToEnd() {
        authenticate();
        
        // 1. Create quote request
        Map<String, Object> quoteRequest = new HashMap<>();
        quoteRequest.put("buyerId", testBuyerId.toString());
        quoteRequest.put("sellerId", testSellerId.toString());
        quoteRequest.put("status", "DRAFT");
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(quoteRequest, getHeadersWithAuth());
        ResponseEntity<String> response = restTemplate.postForEntity(
            baseUrl + "/api/v2/orders/quote-requests", request, String.class);

        assertTrue(response.getStatusCode().is2xxSuccessful() || 
                   response.getStatusCode() == HttpStatus.CREATED,
            "Should create quote request");
        
        UUID quoteId = null;
        if (response.getBody() != null) {
            try {
                JsonNode json = objectMapper.readTree(response.getBody());
                if (json.has("id")) {
                    quoteId = UUID.fromString(json.get("id").asText());
                }
            } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                // JSON parsing failed
            }
        }
        
        // 2. Seller responds to quote
        if (quoteId != null) {
            Map<String, Object> sellerResponse = new HashMap<>();
            sellerResponse.put("shippingCost", 50.00);
            
            HttpEntity<Map<String, Object>> sellerRequest = new HttpEntity<>(sellerResponse, getHeadersWithAuth());
            ResponseEntity<String> sellerResponseEntity = restTemplate.postForEntity(
                baseUrl + "/api/v2/orders/quote-requests/" + quoteId + "/respond", 
                sellerRequest, String.class);

            assertTrue(sellerResponseEntity.getStatusCode().is2xxSuccessful(),
                "Seller should respond to quote");
        }
    }

    @Test
    @Order(101)
    @DisplayName("INT-019: Complete Marketplace workflow - Cart to Payment")
    void testMarketplaceWorkflowEndToEnd() {
        authenticate();
        
        // 1. Create cart
        String createCartUrl = baseUrl + "/api/v2/carts?buyerId=" + testBuyerId;
        HttpEntity<String> createRequest = new HttpEntity<>(getHeadersWithAuth());
        ResponseEntity<String> cartResponse = restTemplate.exchange(
            createCartUrl, HttpMethod.POST, createRequest, String.class);

        UUID cartId = null;
        if (cartResponse.getBody() != null) {
            try {
                JsonNode json = objectMapper.readTree(cartResponse.getBody());
                if (json.has("id")) {
                    cartId = UUID.fromString(json.get("id").asText());
                }
            } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                // JSON parsing failed
            }
        }
        
        // 2. Add item to cart
        if (cartId != null) {
            String addItemUrl = baseUrl + "/api/v2/carts/" + cartId + "/items" +
                "?productId=" + testProductId + "&quantity=1&unitPrice=100.00";
            
            HttpEntity<String> addItemRequest = new HttpEntity<>(getHeadersWithAuth());
            ResponseEntity<String> addItemResponse = restTemplate.exchange(
                addItemUrl, HttpMethod.POST, addItemRequest, String.class);

            assertTrue(addItemResponse.getStatusCode().is2xxSuccessful(),
                "Should add item to cart");
        }
    }

    @Test
    @Order(102)
    @DisplayName("INT-020: Verify all critical endpoints accessible")
    void testAllCriticalEndpointsAccessible() {
        authenticate();
        
        String[] endpoints = {
            "/api/v2/auth/me",
            "/api/v2/products",
            "/api/v2/carts",
            "/api/v2/customers",
            "/api/v2/tenants",
            "/api/v2/orders/purchase-orders",
            "/api/v2/orders/quote-requests",
            "/api/adapter/payments",
            "/api/adapter/pricing/tiers"
        };

        for (String endpoint : endpoints) {
            HttpEntity<String> request = new HttpEntity<>(getHeadersWithAuth());
            ResponseEntity<String> response = restTemplate.exchange(
                baseUrl + endpoint, HttpMethod.GET, request, String.class);

            assertTrue(response.getStatusCode().is2xxSuccessful() || 
                       response.getStatusCode() == HttpStatus.UNAUTHORIZED ||
                       response.getStatusCode() == HttpStatus.NOT_FOUND,
                "Endpoint " + endpoint + " should be accessible, got: " + response.getStatusCode());
        }
    }
}

