package com.quorion.b2b.repository;

import com.quorion.b2b.model.commerce.DirectCheckout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DirectCheckoutRepository extends JpaRepository<DirectCheckout, UUID> {
    List<DirectCheckout> findByBuyerId(UUID buyerId);
    List<DirectCheckout> findByCartId(UUID cartId);
}
