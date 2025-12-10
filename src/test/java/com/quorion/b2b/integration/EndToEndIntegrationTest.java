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

import static org.assertj.core.api.Assertions.assertThat;

/**
 * End-to-End Integration Tests for Quorion B2B Platform
 * Tests the complete workflow from authentication to order completion
 */
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = com.quorion.b2b.QuorionB2bApplication.class
)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EndToEndIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private String baseUrl;
    private String accessToken;
    private String tenantId;
    private String userId;
    private String productId;

    @BeforeAll
    void setup() {
        baseUrl = "http://localhost:" + port;
    }

    // ========== 1. AUTHENTICATION TESTS ==========

    @Test
    @Order(1)
    @DisplayName("1.1 - User should be able to login and receive JWT token")
    void testLoginSuccess() throws Exception {
        // Given
        String loginUrl = baseUrl + "/api/v2/auth/login";
        String requestBody = """
                {
                    "usernameOrEmail": "admin",
                    "password": "password123"
                }
                """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(loginUrl, request, String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        JsonNode jsonResponse = objectMapper.readTree(response.getBody());
        assertThat(jsonResponse.has("accessToken")).isTrue();
        assertThat(jsonResponse.has("refreshToken")).isTrue();
        assertThat(jsonResponse.get("tokenType").asText()).isEqualTo("Bearer");

        // Store token for subsequent tests
        accessToken = jsonResponse.get("accessToken").asText();
        userId = jsonResponse.get("user").get("id").asText();
        tenantId = jsonResponse.get("user").get("tenantId").asText();

        assertThat(accessToken).isNotEmpty();
        assertThat(userId).isNotEmpty();
        assertThat(tenantId).isNotEmpty();
    }

    @Test
    @Order(2)
    @DisplayName("1.2 - Authenticated user should be able to access protected endpoint")
    void testAccessProtectedEndpoint() {
        // Given
        String meUrl = baseUrl + "/api/v2/auth/me";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> request = new HttpEntity<>(headers);

        // When
        ResponseEntity<String> response = restTemplate.exchange(meUrl, HttpMethod.GET, request, String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @Order(3)
    @DisplayName("1.3 - Unauthenticated request should be rejected")
    void testUnauthenticatedRequestRejected() {
        // Given
        String productsUrl = baseUrl + "/api/v2/products";

        // When
        ResponseEntity<String> response = restTemplate.getForEntity(productsUrl, String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    // ========== 2. PRODUCT MANAGEMENT TESTS ==========

    @Test
    @Order(10)
    @DisplayName("2.1 - Should list all products")
    void testListProducts() {
        // Given
        String productsUrl = baseUrl + "/api/v2/products";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> request = new HttpEntity<>(headers);

        // When
        ResponseEntity<String> response = restTemplate.exchange(productsUrl, HttpMethod.GET, request, String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @Order(11)
    @DisplayName("2.2 - Should create a new product")
    void testCreateProduct() throws Exception {
        // Given
        String productsUrl = baseUrl + "/api/v2/products";
        String requestBody = String.format("""
                {
                    "name": "Integration Test Product",
                    "description": "Product created during integration test",
                    "category": "Test Category",
                    "sellerId": "%s"
                }
                """, tenantId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(productsUrl, request, String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        JsonNode jsonResponse = objectMapper.readTree(response.getBody());
        assertThat(jsonResponse.has("id")).isTrue();
        assertThat(jsonResponse.get("name").asText()).isEqualTo("Integration Test Product");

        // Store product ID for subsequent tests
        productId = jsonResponse.get("id").asText();
    }

    @Test
    @Order(12)
    @DisplayName("2.3 - Should retrieve product by ID")
    void testGetProductById() {
        // Given
        String productUrl = baseUrl + "/api/v2/products/" + productId;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> request = new HttpEntity<>(headers);

        // When
        ResponseEntity<String> response = restTemplate.exchange(productUrl, HttpMethod.GET, request, String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    // ========== 3. TENANT MANAGEMENT TESTS ==========

    @Test
    @Order(20)
    @DisplayName("3.1 - Should list all tenants")
    void testListTenants() {
        // Given
        String tenantsUrl = baseUrl + "/api/v2/tenants";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> request = new HttpEntity<>(headers);

        // When
        ResponseEntity<String> response = restTemplate.exchange(tenantsUrl, HttpMethod.GET, request, String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    // ========== 4. CUSTOMER MANAGEMENT TESTS ==========

    @Test
    @Order(30)
    @DisplayName("4.1 - Should list all customers")
    void testListCustomers() {
        // Given
        String customersUrl = baseUrl + "/api/v2/customers";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> request = new HttpEntity<>(headers);

        // When
        ResponseEntity<String> response = restTemplate.exchange(customersUrl, HttpMethod.GET, request, String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    // ========== 5. CART OPERATIONS TESTS ==========

    @Test
    @Order(40)
    @DisplayName("5.1 - Should list all carts")
    void testListCarts() {
        // Given
        String cartsUrl = baseUrl + "/api/v2/carts";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> request = new HttpEntity<>(headers);

        // When
        ResponseEntity<String> response = restTemplate.exchange(cartsUrl, HttpMethod.GET, request, String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    // ========== 6. ORDER MANAGEMENT TESTS ==========

    @Test
    @Order(50)
    @DisplayName("6.1 - Should list all purchase orders")
    void testListPurchaseOrders() {
        // Given
        String ordersUrl = baseUrl + "/api/v2/orders/purchase-orders";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> request = new HttpEntity<>(headers);

        // When
        ResponseEntity<String> response = restTemplate.exchange(ordersUrl, HttpMethod.GET, request, String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @Order(51)
    @DisplayName("6.2 - Should list all quote requests")
    void testListQuoteRequests() {
        // Given
        String quoteRequestsUrl = baseUrl + "/api/v2/orders/quote-requests";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> request = new HttpEntity<>(headers);

        // When
        ResponseEntity<String> response = restTemplate.exchange(quoteRequestsUrl, HttpMethod.GET, request, String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    // ========== 7. PAYMENT MANAGEMENT TESTS ==========

    @Test
    @Order(60)
    @DisplayName("7.1 - Should list all payments")
    void testListPayments() {
        // Given
        String paymentsUrl = baseUrl + "/api/adapter/payments";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> request = new HttpEntity<>(headers);

        // When
        ResponseEntity<String> response = restTemplate.exchange(paymentsUrl, HttpMethod.GET, request, String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    // ========== 8. PRICING MANAGEMENT TESTS ==========

    @Test
    @Order(70)
    @DisplayName("8.1 - Should list all price tiers")
    void testListPriceTiers() {
        // Given
        String priceTiersUrl = baseUrl + "/api/adapter/pricing/tiers";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> request = new HttpEntity<>(headers);

        // When
        ResponseEntity<String> response = restTemplate.exchange(priceTiersUrl, HttpMethod.GET, request, String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @Order(71)
    @DisplayName("8.2 - Should list all list prices")
    void testListListPrices() {
        // Given
        String listPricesUrl = baseUrl + "/api/adapter/pricing/list-prices";
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> request = new HttpEntity<>(headers);

        // When
        ResponseEntity<String> response = restTemplate.exchange(listPricesUrl, HttpMethod.GET, request, String.class);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
