package com.quorion.b2b.repository;

import com.quorion.b2b.model.tenant.SellerMarketplace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface SellerMarketplaceRepository extends JpaRepository<SellerMarketplace, UUID> {
    List<SellerMarketplace> findBySellerId(UUID sellerId);
    List<SellerMarketplace> findByIsActive(Boolean isActive);
    List<SellerMarketplace> findBySellerIdAndIsActive(UUID sellerId, Boolean isActive);
}
