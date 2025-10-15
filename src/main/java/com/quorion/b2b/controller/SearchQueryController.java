package com.quorion.b2b.controller;

import com.quorion.b2b.model.commerce.SearchQuery;
import jakarta.validation.Valid;
import com.quorion.b2b.repository.SearchQueryRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
/**
 * SearchQuery Controller
 */
@RestController
@RequestMapping("/api/search-queries")
@RequiredArgsConstructor
@Tag(name = "SearchQuery", description = "SearchQuery management")
public class SearchQueryController {
    private final SearchQueryRepository searchqueryRepository;
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "List all search-queries")
    public ResponseEntity<List<SearchQuery>> getAll() {
        return ResponseEntity.ok(searchqueryRepository.findAll());
    }
    @GetMapping("/{id}")
    @Operation(summary = "Get searchquery by ID")
    public ResponseEntity<SearchQuery> getById(@PathVariable UUID id) {
        return searchqueryRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create searchquery")
    public ResponseEntity<SearchQuery> create(@Valid @RequestBody SearchQuery searchquery) {
        return ResponseEntity.status(HttpStatus.CREATED).body(searchqueryRepository.save(searchquery));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update searchquery")
    public ResponseEntity<SearchQuery> update(@PathVariable UUID id, @RequestBody SearchQuery details) {
        return searchqueryRepository.findById(id)
                .map(existing -> {
                    details.setId(id);
                    return ResponseEntity.ok(searchqueryRepository.save(details));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete searchquery")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        if (searchqueryRepository.existsById(id)) {
            searchqueryRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
