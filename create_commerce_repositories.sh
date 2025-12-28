#!/bin/bash

# Commerce repositories
cat > src/main/java/com/quorion/b2b/repository/NotificationRepository.java << 'JAVA'
package com.quorion.b2b.repository;

import com.quorion.b2b.model.commerce.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findByRecipientId(UUID recipientId);
    List<Notification> findByRecipientIdAndIsReadFalse(UUID recipientId);
}
JAVA

cat > src/main/java/com/quorion/b2b/repository/PaymentRepository.java << 'JAVA'
package com.quorion.b2b.repository;

import com.quorion.b2b.model.commerce.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    List<Payment> findByOrderId(UUID orderId);
}
JAVA

cat > src/main/java/com/quorion/b2b/repository/DirectCheckoutRepository.java << 'JAVA'
package com.quorion.b2b.repository;

import com.quorion.b2b.model.commerce.DirectCheckout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DirectCheckoutRepository extends JpaRepository<DirectCheckout, UUID> {
    List<DirectCheckout> findByBuyerId(UUID buyerId);
    List<DirectCheckout> findByCartId(UUID cartId);
}
JAVA

cat > src/main/java/com/quorion/b2b/repository/ProductViewRepository.java << 'JAVA'
package com.quorion.b2b.repository;

import com.quorion.b2b.model.commerce.ProductView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductViewRepository extends JpaRepository<ProductView, UUID> {
    List<ProductView> findByProductId(UUID productId);
    List<ProductView> findByViewerId(UUID viewerId);
}
JAVA

cat > src/main/java/com/quorion/b2b/repository/SearchQueryRepository.java << 'JAVA'
package com.quorion.b2b.repository;

import com.quorion.b2b.model.commerce.SearchQuery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SearchQueryRepository extends JpaRepository<SearchQuery, UUID> {
    List<SearchQuery> findBySearcherId(UUID searcherId);
}
JAVA

cat > src/main/java/com/quorion/b2b/repository/PromoCodeRepository.java << 'JAVA'
package com.quorion.b2b.repository;

import com.quorion.b2b.model.commerce.PromoCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PromoCodeRepository extends JpaRepository<PromoCode, UUID> {
    Optional<PromoCode> findByCode(String code);
}
JAVA

cat > src/main/java/com/quorion/b2b/repository/SellerRatingRepository.java << 'JAVA'
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
JAVA

cat > src/main/java/com/quorion/b2b/repository/CartItemRepository.java << 'JAVA'
package com.quorion.b2b.repository;

import com.quorion.b2b.model.commerce.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, UUID> {
    List<CartItem> findByCartId(UUID cartId);
}
JAVA

echo "Commerce repositories created"
