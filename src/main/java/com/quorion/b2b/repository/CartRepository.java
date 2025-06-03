package com.quorion.b2b.repository;

import com.quorion.b2b.model.commerce.Cart;
import com.quorion.b2b.model.tenant.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Cart entity
 */
@Repository
public interface CartRepository extends JpaRepository<Cart, UUID> {

    List<Cart> findByBuyerId(UUID buyerId);

    List<Cart> findByIsActive(Boolean isActive);

    List<Cart> findByBuyerIdAndIsActive(UUID buyerId, Boolean isActive);

    List<Cart> findByBuyerAndIsActiveTrue(Tenant buyer);

    Optional<Cart> findBySessionKeyAndIsActiveTrue(String sessionKey);

    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.items WHERE c.id = :id")
    Optional<Cart> findByIdWithItems(UUID id);

    @Query("SELECT c FROM Cart c WHERE c.expiresAt < CURRENT_TIMESTAMP AND c.isActive = true")
    List<Cart> findExpiredCarts();
}
