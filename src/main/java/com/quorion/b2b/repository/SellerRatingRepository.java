package com.quorion.b2b.repository;

import com.quorion.b2b.model.commerce.SellerRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SellerRatingRepository extends JpaRepository<SellerRating, UUID> {
    List<SellerRating> findBySellerId(UUID sellerId);
    List<SellerRating> findBySellerIdAndIsApprovedTrue(UUID sellerId);
}
