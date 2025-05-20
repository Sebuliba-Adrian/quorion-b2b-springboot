package com.quorion.b2b.repository;

import com.quorion.b2b.model.product.Product;
import com.quorion.b2b.model.product.ProductStatus;
import com.quorion.b2b.model.tenant.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for Product entity
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    List<Product> findBySeller(Tenant seller);

    List<Product> findBySellerAndStatus(Tenant seller, ProductStatus status);

    List<Product> findBySellerAndIsActiveTrue(Tenant seller);

    @Query("SELECT p FROM Product p WHERE p.seller = :seller AND p.status = 'PUBLISHED' AND p.isActive = true")
    List<Product> findPublishedProductsBySeller(Tenant seller);
}
