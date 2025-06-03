package com.quorion.b2b.repository;

import com.quorion.b2b.model.commerce.QuoteRequest;
import com.quorion.b2b.model.commerce.QuoteStatus;
import com.quorion.b2b.model.tenant.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for QuoteRequest entity
 */
@Repository
public interface QuoteRequestRepository extends JpaRepository<QuoteRequest, UUID> {

    Optional<QuoteRequest> findByNumber(String number);

    List<QuoteRequest> findByBuyer(Tenant buyer);

    List<QuoteRequest> findBySeller(Tenant seller);

    List<QuoteRequest> findByBuyerAndStatus(Tenant buyer, QuoteStatus status);

    List<QuoteRequest> findBySellerAndStatus(Tenant seller, QuoteStatus status);

    @Query("SELECT q FROM QuoteRequest q LEFT JOIN FETCH q.items WHERE q.id = :id")
    Optional<QuoteRequest> findByIdWithItems(UUID id);
}
