package com.quorion.b2b.security.permissions;

import com.quorion.b2b.model.User;
import com.quorion.b2b.model.tenant.TenantType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Custom Permission Evaluator for RBAC
 */
@Component("customPermissionEvaluator")
@Slf4j
public class CustomPermissionEvaluator {

    /**
     * Check if user is authenticated
     */
    public boolean isAuthenticated(Authentication authentication) {
        return authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal());
    }

    /**
     * Check if user belongs to a tenant
     */
    public boolean isTenantUser(Authentication authentication) {
        if (!isAuthenticated(authentication)) {
            return false;
        }
        User user = (User) authentication.getPrincipal();
        return user.getTenant() != null;
    }

    /**
     * Check if user is a seller
     */
    public boolean isSeller(Authentication authentication) {
        if (!isAuthenticated(authentication)) {
            return false;
        }
        User user = (User) authentication.getPrincipal();
        return user.getTenant() != null && user.getTenant().getType() == TenantType.SELLER;
    }

    /**
     * Check if user is a buyer
     */
    public boolean isBuyer(Authentication authentication) {
        if (!isAuthenticated(authentication)) {
            return false;
        }
        User user = (User) authentication.getPrincipal();
        return user.getTenant() != null && user.getTenant().getType() == TenantType.BUYER;
    }

    /**
     * Check if user is a distributor
     */
    public boolean isDistributor(Authentication authentication) {
        if (!isAuthenticated(authentication)) {
            return false;
        }
        User user = (User) authentication.getPrincipal();
        return user.getTenant() != null && user.getTenant().getType() == TenantType.DISTRIBUTOR;
    }

    /**
     * Check if user is a seller or distributor
     */
    public boolean isSellerOrDistributor(Authentication authentication) {
        return isSeller(authentication) || isDistributor(authentication);
    }

    /**
     * Check if user is a buyer or seller
     */
    public boolean isBuyerOrSeller(Authentication authentication) {
        return isBuyer(authentication) || isSeller(authentication);
    }

    /**
     * Check if user owns the tenant
     */
    public boolean isTenantOwner(Authentication authentication, UUID tenantId) {
        if (!isAuthenticated(authentication)) {
            return false;
        }
        User user = (User) authentication.getPrincipal();
        return user.getTenant() != null && user.getTenant().getId().equals(tenantId);
    }

    /**
     * Check if user can access a resource based on tenant ownership
     */
    public boolean canAccessResource(Authentication authentication, UUID resourceTenantId) {
        if (!isAuthenticated(authentication)) {
            return false;
        }
        User user = (User) authentication.getPrincipal();
        return user.getTenant() != null && user.getTenant().getId().equals(resourceTenantId);
    }

    /**
     * Check if user is seller of a specific product
     */
    public boolean isSellerOfProduct(Authentication authentication, UUID productTenantId) {
        return isSeller(authentication) && canAccessResource(authentication, productTenantId);
    }

    /**
     * Check if user can access an order (buyer or seller)
     */
    public boolean canAccessOrder(Authentication authentication, UUID buyerTenantId, UUID sellerTenantId) {
        if (!isAuthenticated(authentication)) {
            return false;
        }
        User user = (User) authentication.getPrincipal();
        if (user.getTenant() == null) {
            return false;
        }
        UUID userTenantId = user.getTenant().getId();
        return userTenantId.equals(buyerTenantId) || userTenantId.equals(sellerTenantId);
    }

    /**
     * Check if user can access a quote (buyer or seller)
     */
    public boolean canAccessQuote(Authentication authentication, UUID buyerTenantId, UUID sellerTenantId) {
        return canAccessOrder(authentication, buyerTenantId, sellerTenantId);
    }

    /**
     * Check if user can access a lead (buyer or seller)
     */
    public boolean canAccessLead(Authentication authentication, UUID buyerTenantId, UUID sellerTenantId) {
        return canAccessOrder(authentication, buyerTenantId, sellerTenantId);
    }

    /**
     * Check if user is owner or has read-only access
     */
    public boolean isOwnerOrReadOnly(Authentication authentication, UUID ownerId, String httpMethod) {
        if (!isAuthenticated(authentication)) {
            return false;
        }

        // Read operations are always allowed for authenticated users
        if ("GET".equalsIgnoreCase(httpMethod) || "HEAD".equalsIgnoreCase(httpMethod)) {
            return true;
        }

        // Write operations require ownership
        User user = (User) authentication.getPrincipal();
        return user.getTenant() != null && user.getTenant().getId().equals(ownerId);
    }
}
