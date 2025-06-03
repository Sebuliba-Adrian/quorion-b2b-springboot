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
