package com.quorion.b2b.repository;

import com.quorion.b2b.model.commerce.ShipmentAdvice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface ShipmentAdviceRepository extends JpaRepository<ShipmentAdvice, UUID> {
}
