package com.quorion.b2b.controller;

import com.quorion.b2b.model.tenant.Tenant;
import com.quorion.b2b.model.tenant.TenantType;
import com.quorion.b2b.model.User;
import com.quorion.b2b.repository.TenantRepository;
import com.quorion.b2b.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Test Controller for bootstrapping data
 */
@RestController
@RequestMapping("/api/public/test")
@RequiredArgsConstructor
public class TestController {
    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "Test endpoint is accessible");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/bootstrap")
    public ResponseEntity<Map<String, String>> bootstrap() {
        Map<String, String> response = new HashMap<>();
        try {
            // Create test tenants if they don't exist
            if (tenantRepository.count() == 0) {
                Tenant seller = Tenant.builder()
                        .name("Test Seller")
                        .type(TenantType.SELLER)
                        .contactEmail("seller@test.com")
                        .isActive(true)
                        .build();
                tenantRepository.save(seller);

                Tenant buyer = Tenant.builder()
                        .name("Test Buyer")
                        .type(TenantType.BUYER)
                        .contactEmail("buyer@test.com")
                        .isActive(true)
                        .build();
                tenantRepository.save(buyer);

                // Create test user
                User user = new User();
                user.setUsername("testuser");
                user.setEmail("test@example.com");
                user.setPassword(passwordEncoder.encode("Test123456"));
                user.setFirstName("Test");
                user.setLastName("User");
                user.setTenant(seller);
                user.setIsActive(true);
                userRepository.save(user);

                response.put("status", "success");
                response.put("message", "Bootstrap data created successfully");
            } else {
                response.put("status", "skipped");
                response.put("message", "Data already exists");
            }
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
        }
        return ResponseEntity.ok(response);
    }
}
