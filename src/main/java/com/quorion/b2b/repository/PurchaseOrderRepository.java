package com.quorion.b2b.repository;

import com.quorion.b2b.model.commerce.OrderStatus;
import com.quorion.b2b.model.commerce.PurchaseOrder;
import com.quorion.b2b.model.tenant.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for PurchaseOrder entity
 */
@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, UUID> {

    Optional<PurchaseOrder> findByNumber(String number);

    List<PurchaseOrder> findByBuyer(Tenant buyer);

    List<PurchaseOrder> findBySeller(Tenant seller);

    List<PurchaseOrder> findByBuyerAndStatus(Tenant buyer, OrderStatus status);

    List<PurchaseOrder> findBySellerAndStatus(Tenant seller, OrderStatus status);

    @Query("SELECT o FROM PurchaseOrder o LEFT JOIN FETCH o.items WHERE o.id = :id")
    Optional<PurchaseOrder> findByIdWithItems(UUID id);
}
