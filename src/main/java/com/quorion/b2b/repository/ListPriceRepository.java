package com.quorion.b2b.repository;

import com.quorion.b2b.model.product.ListPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface ListPriceRepository extends JpaRepository<ListPrice, UUID> {
    List<ListPrice> findBySkuId(UUID skuId);
    List<ListPrice> findByCurrency(String currency);
    List<ListPrice> findByIsActive(Boolean isActive);
    List<ListPrice> findBySkuIdAndCurrencyAndIsActive(UUID skuId, String currency, Boolean isActive);
}
