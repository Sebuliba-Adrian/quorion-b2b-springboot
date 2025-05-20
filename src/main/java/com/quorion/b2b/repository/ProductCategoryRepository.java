package com.quorion.b2b.repository;

import com.quorion.b2b.model.product.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, UUID> {
    Optional<ProductCategory> findBySlug(String slug);
    List<ProductCategory> findByParentIsNull();
    List<ProductCategory> findByParentId(UUID parentId);
    List<ProductCategory> findByIsActive(Boolean isActive);
}
