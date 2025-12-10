package com.quorion.b2b.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Smoke Tests - Critical Path Verification with Real HTTP Calls
 * 
 * Tests the most critical endpoints using actual HTTP requests
 * Runs quickly to verify system health
 */
@SpringBootTest(
    classes = com.quorion.b2b.TestApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "spring.main.allow-bean-definition-overriding=true"
    }
)
@ActiveProfiles("test")
// Legacy controllers excluded via TestApplication
@org.springframework.test.context.jdbc.Sql(scripts = "/data-test.sql", executionPhase = org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@DisplayName("Smoke Tests - Critical Path (Real HTTP)")
public class SmokeTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String getBaseUrl() {
        return "http://localhost:" + port;
    }

    @Test
    @DisplayName("SMOKE-001: Application starts and health check")
    void testApplicationHealth() {
        assertNotNull(restTemplate, "TestRestTemplate should be available");
        assertTrue(port > 0, "Server should be running on a port");
    }

    @Test
    @DisplayName("SMOKE-002: Auth endpoint accessible via HTTP")
    void testAuthEndpointAccessible() {
        ResponseEntity<String> response = restTemplate.getForEntity(
            getBaseUrl() + "/api/v2/auth/me", String.class);
        
        // Should return 401 (unauthorized) or 200 (if authenticated), not 404 or 500
        assertTrue(response.getStatusCode() == HttpStatus.UNAUTHORIZED || 
                   response.getStatusCode() == HttpStatus.OK,
            "Auth endpoint should be accessible, got status: " + response.getStatusCode());
        assertNotEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), 
            "Endpoint should exist");
        assertNotEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode(), 
            "Endpoint should not have server errors");
    }

    @Test
    @DisplayName("SMOKE-003: Products endpoint accessible via HTTP")
    void testProductsEndpointAccessible() {
        ResponseEntity<String> response = restTemplate.getForEntity(
            getBaseUrl() + "/api/v2/products", String.class);
        
        assertTrue(response.getStatusCode() == HttpStatus.UNAUTHORIZED || 
                   response.getStatusCode() == HttpStatus.OK,
            "Products endpoint should be accessible");
        assertNotEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @DisplayName("SMOKE-004: Carts endpoint accessible via HTTP")
    void testCartsEndpointAccessible() {
        ResponseEntity<String> response = restTemplate.getForEntity(
            getBaseUrl() + "/api/v2/carts", String.class);
        
        assertTrue(response.getStatusCode() == HttpStatus.UNAUTHORIZED || 
                   response.getStatusCode() == HttpStatus.OK,
            "Carts endpoint should be accessible");
        assertNotEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @DisplayName("SMOKE-005: Payments endpoint accessible via HTTP")
    void testPaymentsEndpointAccessible() {
        ResponseEntity<String> response = restTemplate.getForEntity(
            getBaseUrl() + "/api/adapter/payments", String.class);
        
        assertTrue(response.getStatusCode() == HttpStatus.UNAUTHORIZED || 
                   response.getStatusCode() == HttpStatus.OK,
            "Payments endpoint should be accessible");
        assertNotEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @DisplayName("SMOKE-006: Pricing endpoint accessible via HTTP")
    void testPricingEndpointAccessible() {
        ResponseEntity<String> response = restTemplate.getForEntity(
            getBaseUrl() + "/api/adapter/pricing/tiers", String.class);
        
        assertTrue(response.getStatusCode() == HttpStatus.UNAUTHORIZED || 
                   response.getStatusCode() == HttpStatus.OK,
            "Pricing endpoint should be accessible");
        assertNotEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @DisplayName("SMOKE-007: Orders endpoint accessible via HTTP")
    void testOrdersEndpointAccessible() {
        ResponseEntity<String> response = restTemplate.getForEntity(
            getBaseUrl() + "/api/v2/orders/purchase-orders", String.class);
        
        assertTrue(response.getStatusCode() == HttpStatus.UNAUTHORIZED || 
                   response.getStatusCode() == HttpStatus.OK,
            "Orders endpoint should be accessible");
        assertNotEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @DisplayName("SMOKE-008: Tenants endpoint accessible via HTTP")
    void testTenantsEndpointAccessible() {
        ResponseEntity<String> response = restTemplate.getForEntity(
            getBaseUrl() + "/api/v2/tenants", String.class);
        
        assertTrue(response.getStatusCode() == HttpStatus.UNAUTHORIZED || 
                   response.getStatusCode() == HttpStatus.OK,
            "Tenants endpoint should be accessible");
        assertNotEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @DisplayName("SMOKE-009: Customers endpoint accessible via HTTP")
    void testCustomersEndpointAccessible() {
        ResponseEntity<String> response = restTemplate.getForEntity(
            getBaseUrl() + "/api/v2/customers", String.class);
        
        assertTrue(response.getStatusCode() == HttpStatus.UNAUTHORIZED || 
                   response.getStatusCode() == HttpStatus.OK,
            "Customers endpoint should be accessible");
        assertNotEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @DisplayName("SMOKE-010: All critical endpoints respond via HTTP (not 404/500)")
    void testAllCriticalEndpointsRespond() {
        String[] endpoints = {
            "/api/v2/auth/me",
            "/api/v2/products",
            "/api/v2/carts",
            "/api/v2/orders/purchase-orders",
            "/api/v2/customers",
            "/api/v2/tenants",
            "/api/adapter/payments",
            "/api/adapter/pricing/tiers"
        };

        for (String endpoint : endpoints) {
            ResponseEntity<String> response = restTemplate.getForEntity(
                getBaseUrl() + endpoint, String.class);
            
            assertNotEquals(HttpStatus.NOT_FOUND, response.getStatusCode(), 
                "Endpoint " + endpoint + " should not return 404");
            assertNotEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode(), 
                "Endpoint " + endpoint + " should not return 500");
        }
    }
}

