package com.quorion.b2b.repository;

import com.quorion.b2b.model.product.Product;
import com.quorion.b2b.model.product.ProductSKU;
import com.quorion.b2b.model.product.SKUKind;
import com.quorion.b2b.model.tenant.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for ProductSKU entity
 */
@Repository
public interface ProductSKURepository extends JpaRepository<ProductSKU, UUID> {

    List<ProductSKU> findByProduct(Product product);

    Optional<ProductSKU> findByNumber(String number);

    List<ProductSKU> findByKind(SKUKind kind);

    List<ProductSKU> findByDistributor(Tenant distributor);

    List<ProductSKU> findByProductAndIsActiveTrue(Product product);
}
