package com.quorion.b2b.repository;

import com.quorion.b2b.model.product.ProductAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductAttributeRepository extends JpaRepository<ProductAttribute, UUID> {
    Optional<ProductAttribute> findBySlug(String slug);
}
