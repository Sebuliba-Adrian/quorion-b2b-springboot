package com.quorion.b2b.repository;

import com.quorion.b2b.model.commerce.ProductView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductViewRepository extends JpaRepository<ProductView, UUID> {
    List<ProductView> findByProductId(UUID productId);
    List<ProductView> findByViewerId(UUID viewerId);
}
