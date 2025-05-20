package com.quorion.b2b.repository;

import com.quorion.b2b.model.product.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, UUID> {
    List<Wishlist> findByBuyerId(UUID buyerId);
    List<Wishlist> findByBuyerIdAndIsActiveTrue(UUID buyerId);
}
