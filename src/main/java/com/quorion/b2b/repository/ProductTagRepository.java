package com.quorion.b2b.repository;

import com.quorion.b2b.model.product.ProductTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductTagRepository extends JpaRepository<ProductTag, UUID> {
    Optional<ProductTag> findBySlug(String slug);
}
