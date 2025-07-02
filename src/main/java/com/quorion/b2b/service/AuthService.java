package com.quorion.b2b.service;

import com.quorion.b2b.dto.auth.*;
import com.quorion.b2b.model.tenant.Tenant;
import com.quorion.b2b.model.User;
import com.quorion.b2b.repository.TenantRepository;
import com.quorion.b2b.repository.UserRepository;
import com.quorion.b2b.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Authentication Service
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    /**
     * Register a new user
     */
    @Transactional
    public LoginResponse register(RegisterRequest request) {
        // Check if username exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new jakarta.persistence.EntityNotFoundException("Username is already taken");
        }

        // Check if email exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new jakarta.persistence.EntityNotFoundException("Email is already in use");
        }

        // Verify tenant exists
        Tenant tenant = tenantRepository.findById(request.getTenantId())
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("Tenant not found"));

        // Create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setTenant(tenant);
        user.setIsActive(true);

        User savedUser = userRepository.save(user);
        log.info("New user registered: {}", savedUser.getUsername());

        // Authenticate the newly created user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate tokens
        String accessToken = tokenProvider.generateAccessToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(authentication);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .user(mapUserToResponse(savedUser))
                .build();
    }

    /**
     * Authenticate user and return tokens
     */
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsernameOrEmail(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Get user details
        User user = (User) authentication.getPrincipal();

        // Check if user is active
        if (!user.getIsActive()) {
            throw new jakarta.persistence.EntityNotFoundException("User account is inactive");
        }

        // Generate tokens
        String accessToken = tokenProvider.generateAccessToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(authentication);

        log.info("User logged in: {}", user.getUsername());

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .user(mapUserToResponse(user))
                .build();
    }

    /**
     * Refresh access token using refresh token
     */
    @Transactional(readOnly = true)
    public LoginResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        // Validate refresh token
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new jakarta.persistence.EntityNotFoundException("Invalid refresh token");
        }

        // Check if it's actually a refresh token
        String tokenType = tokenProvider.getTokenType(refreshToken);
        if (!"refresh".equals(tokenType)) {
            throw new jakarta.persistence.EntityNotFoundException("Token is not a refresh token");
        }

        // Get user from token
        UUID userId = tokenProvider.getUserIdFromToken(refreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("User not found"));

        // Check if user is active
        if (!user.getIsActive()) {
            throw new jakarta.persistence.EntityNotFoundException("User account is inactive");
        }

        // Create authentication object
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities()
        );

        // Generate new tokens
        String newAccessToken = tokenProvider.generateAccessToken(authentication);
        String newRefreshToken = tokenProvider.generateRefreshToken(authentication);

        log.info("Tokens refreshed for user: {}", user.getUsername());

        return LoginResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .user(mapUserToResponse(user))
                .build();
    }

    /**
     * Get current authenticated user
     */
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new jakarta.persistence.EntityNotFoundException("No authenticated user found");
        }

        User user = (User) authentication.getPrincipal();
        return mapUserToResponse(user);
    }

    /**
     * Map User entity to UserResponse DTO
     */
    private UserResponse mapUserToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .isActive(user.getIsActive())
                .tenantId(user.getTenant() != null ? user.getTenant().getId() : null)
                .tenantName(user.getTenant() != null ? user.getTenant().getName() : null)
                .tenantType(user.getTenant() != null ? user.getTenant().getType() : null)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
