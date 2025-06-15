package com.quorion.b2b.repository;

import com.quorion.b2b.model.commerce.PaymentTerm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface PaymentTermRepository extends JpaRepository<PaymentTerm, UUID> {
}
