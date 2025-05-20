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
