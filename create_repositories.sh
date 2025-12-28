#!/bin/bash

# Create repository directory if it doesn't exist
mkdir -p src/main/java/com/quorion/b2b/repository

# Product repositories
cat > src/main/java/com/quorion/b2b/repository/ProductCategoryRepository.java << 'JAVA'
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
JAVA

cat > src/main/java/com/quorion/b2b/repository/ProductImageRepository.java << 'JAVA'
package com.quorion.b2b.repository;

import com.quorion.b2b.model.product.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, UUID> {
    List<ProductImage> findByProductId(UUID productId);
    List<ProductImage> findByProductIdAndIsPrimaryTrue(UUID productId);
}
JAVA

cat > src/main/java/com/quorion/b2b/repository/ProductTagRepository.java << 'JAVA'
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
JAVA

cat > src/main/java/com/quorion/b2b/repository/ProductAttributeRepository.java << 'JAVA'
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
JAVA

cat > src/main/java/com/quorion/b2b/repository/ProductAttributeValueRepository.java << 'JAVA'
package com.quorion.b2b.repository;

import com.quorion.b2b.model.product.ProductAttributeValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductAttributeValueRepository extends JpaRepository<ProductAttributeValue, UUID> {
    List<ProductAttributeValue> findByAttributeId(UUID attributeId);
}
JAVA

cat > src/main/java/com/quorion/b2b/repository/ProductVariantRepository.java << 'JAVA'
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
JAVA

cat > src/main/java/com/quorion/b2b/repository/ProductReviewRepository.java << 'JAVA'
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
JAVA

cat > src/main/java/com/quorion/b2b/repository/WishlistRepository.java << 'JAVA'
package com.quorion.b2b.repository;

import com.quorion.b2b.model.product.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, UUID> {
    List<Wishlist> findByBuyerId(UUID buyerId);
    List<Wishlist> findByBuyerIdAndIsActiveTrue(UUID buyerId);
}
JAVA

cat > src/main/java/com/quorion/b2b/repository/WishlistItemRepository.java << 'JAVA'
package com.quorion.b2b.repository;

import com.quorion.b2b.model.product.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WishlistItemRepository extends JpaRepository<WishlistItem, UUID> {
    List<WishlistItem> findByWishlistId(UUID wishlistId);
}
JAVA

cat > src/main/java/com/quorion/b2b/repository/InventoryRepository.java << 'JAVA'
package com.quorion.b2b.repository;

import com.quorion.b2b.model.product.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, UUID> {
    List<Inventory> findByProductId(UUID productId);
    List<Inventory> findBySkuId(UUID skuId);
    List<Inventory> findByWarehouseId(UUID warehouseId);
}
JAVA

echo "Product repositories created"
