package com.quorion.b2b.repository;

import com.quorion.b2b.model.product.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WishlistItemRepository extends JpaRepository<WishlistItem, UUID> {
    List<WishlistItem> findByWishlistId(UUID wishlistId);
}
