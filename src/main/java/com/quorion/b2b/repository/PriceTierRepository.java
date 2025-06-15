package com.quorion.b2b.repository;

import com.quorion.b2b.model.commerce.PriceTier;
import com.quorion.b2b.model.product.ProductSKU;
import com.quorion.b2b.model.tenant.Tenant;
import com.quorion.b2b.model.tenant.TenantAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for PriceTier entity
 */
@Repository
public interface PriceTierRepository extends JpaRepository<PriceTier, UUID> {

    List<PriceTier> findBySellerAndBuyer(Tenant seller, Tenant buyer);

    List<PriceTier> findByProductSku(ProductSKU sku);

    // Advanced pricing query methods
    List<PriceTier> findByProductSkuId(UUID skuId);

    List<PriceTier> findByProductSkuIdAndBuyerId(UUID skuId, UUID buyerId);

    List<PriceTier> findByProductSkuIdAndDestinationId(UUID skuId, UUID destinationId);

    List<PriceTier> findByProductSkuIdAndBuyerIdAndDestinationId(UUID skuId, UUID buyerId, UUID destinationId);

    @Query("SELECT pt FROM PriceTier pt WHERE " +
           "pt.seller = :seller AND " +
           "pt.buyer = :buyer AND " +
           "pt.destination = :destination AND " +
           "pt.productSku = :sku AND " +
           "pt.currency = :currency AND " +
           "pt.isActive = true")
    List<PriceTier> findApplicablePriceTiers(
        @Param("seller") Tenant seller,
        @Param("buyer") Tenant buyer,
        @Param("destination") TenantAddress destination,
        @Param("sku") ProductSKU sku,
        @Param("currency") String currency
    );
}
