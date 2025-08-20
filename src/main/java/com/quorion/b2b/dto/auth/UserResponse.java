package com.quorion.b2b.dto.auth;

import com.quorion.b2b.model.tenant.TenantType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * User Response DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private UUID id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private Boolean isActive;
    private UUID tenantId;
    private String tenantName;
    private TenantType tenantType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
