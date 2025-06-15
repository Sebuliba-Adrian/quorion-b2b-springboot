package com.quorion.b2b.repository;

import com.quorion.b2b.model.product.PackagingUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface PackagingUnitRepository extends JpaRepository<PackagingUnit, UUID> {
}
