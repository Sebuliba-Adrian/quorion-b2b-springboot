package com.quorion.b2b.model.commerce;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.quorion.b2b.model.BaseEntity;
import com.quorion.b2b.model.tenant.Tenant;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Track search queries for analytics and recommendations
 */
@Entity
@Table(name = "search_query")
@Data
@EqualsAndHashCode(callSuper = true, exclude = "searcher")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchQuery extends BaseEntity {

    @NotBlank
    @Column(name = "query", nullable = false)
    private String query;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "searcher_id")
    @JsonIgnore
    private Tenant searcher;

    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "results_count", nullable = false)
    @Builder.Default
    private Integer resultsCount = 0;

    @Column(name = "searched_at", nullable = false)
    private LocalDateTime searchedAt;

    @PrePersist
    protected void onCreateSearchQuery() {
        if (searchedAt == null) {
            searchedAt = LocalDateTime.now();
        }
    }
}
