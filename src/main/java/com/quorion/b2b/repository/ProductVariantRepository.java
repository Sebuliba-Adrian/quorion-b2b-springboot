package com.quorion.b2b.repository;

import com.quorion.b2b.model.product.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, UUID> {
    List<ProductVariant> findByProductId(UUID productId);
    List<ProductVariant> findBySkuId(UUID skuId);
    List<ProductVariant> findByIsActive(Boolean isActive);
}
