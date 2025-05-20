package com.quorion.b2b.repository;

import com.quorion.b2b.model.product.ProductReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReview, UUID> {
    List<ProductReview> findByProductId(UUID productId);
    List<ProductReview> findByProductIdAndIsApprovedTrue(UUID productId);
    List<ProductReview> findByBuyerId(UUID buyerId);
}
