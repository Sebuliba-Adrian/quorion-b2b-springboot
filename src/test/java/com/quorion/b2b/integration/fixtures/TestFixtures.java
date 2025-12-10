package com.quorion.b2b.integration.fixtures;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Test Fixtures - Similar to pytest fixtures
 * Provides reusable test utilities and data
 */
public class TestFixtures {

    public static final String TEST_USERNAME = "testuser";
    public static final String TEST_PASSWORD = "Test123456!";
    public static final String TEST_EMAIL = "testuser@example.com";
    
    public static final UUID TEST_TENANT_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    public static final UUID TEST_BUYER_TENANT_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");
    public static final UUID TEST_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000010");

    /**
     * Create authentication headers with JWT token
     */
    public static HttpHeaders createAuthHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (token != null) {
            headers.setBearerAuth(token);
        }
        return headers;
    }

    /**
     * Create login request
     */
    public static Map<String, String> createLoginRequest(String username, String password) {
        Map<String, String> request = new HashMap<>();
        request.put("usernameOrEmail", username);
        request.put("password", password);
        return request;
    }

    /**
     * Authenticate and get token
     */
    public static String authenticate(RestTemplate restTemplate, String baseUrl, ObjectMapper objectMapper) {
        try {
            Map<String, String> loginRequest = createLoginRequest(TEST_USERNAME, TEST_PASSWORD);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, String>> request = new HttpEntity<>(loginRequest, headers);
            
            var response = restTemplate.postForEntity(
                baseUrl + "/api/v2/auth/login", request, String.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                var json = objectMapper.readTree(response.getBody());
                if (json.has("accessToken")) {
                    return json.get("accessToken").asText();
                } else if (json.has("token")) {
                    return json.get("token").asText();
                }
            }
        } catch (Exception e) {
            // Authentication failed
        }
        return null;
    }

    /**
     * Create authenticated HTTP entity
     */
    public static <T> HttpEntity<T> createAuthenticatedEntity(T body, String token) {
        return new HttpEntity<>(body, createAuthHeaders(token));
    }

    /**
     * Create unauthenticated HTTP entity
     */
    public static <T> HttpEntity<T> createEntity(T body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }
}


