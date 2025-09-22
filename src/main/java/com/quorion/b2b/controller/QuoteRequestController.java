package com.quorion.b2b.controller;

import com.quorion.b2b.model.commerce.QuoteRequest;
import jakarta.validation.Valid;
import com.quorion.b2b.service.QuoteRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
/**
 * REST Controller for Quote Request management
 */
@RestController
@RequestMapping("/api/quote-requests")
@RequiredArgsConstructor
@Tag(name = "Quotes", description = "Quote request and negotiation APIs")
public class QuoteRequestController {
    private final QuoteRequestService quoteRequestService;
    @GetMapping
    @Operation(summary = "Get all quote requests")
    public ResponseEntity<List<QuoteRequest>> getAllQuotes() {
        return ResponseEntity.ok(quoteRequestService.findAll());
    }
    @GetMapping("/{id}")
    @Operation(summary = "Get quote request by ID")
    public ResponseEntity<QuoteRequest> getQuoteById(@PathVariable UUID id) {
        return ResponseEntity.ok(quoteRequestService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new quote request")
    public ResponseEntity<QuoteRequest> createQuote(@Valid @RequestBody QuoteRequest quoteRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(quoteRequestService.create(quoteRequest));
    }

    @PostMapping("/{id}/buyer_requests")
    @Operation(summary = "Buyer submits quote request")
    public ResponseEntity<QuoteRequest> buyerRequests(@PathVariable UUID id) {
        return ResponseEntity.ok(quoteRequestService.buyerRequests(id));
    }

    @PostMapping("/{id}/buyer_accepts")
    @Operation(summary = "Buyer accepts quote (creates order)")
    public ResponseEntity<QuoteRequest> buyerAccepts(@PathVariable UUID id) {
        return ResponseEntity.ok(quoteRequestService.buyerAccepts(id));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel quote request")
    public ResponseEntity<QuoteRequest> cancelQuote(@PathVariable UUID id) {
        return ResponseEntity.ok(quoteRequestService.cancel(id));
    }

    @PostMapping("/{id}/seller_responds")
    @Operation(summary = "Seller provides pricing response to quote request")
    public ResponseEntity<QuoteRequest> sellerResponds(@PathVariable UUID id, @Valid @RequestBody com.quorion.b2b.dto.QuoteResponseDTO response) {
        return ResponseEntity.ok(quoteRequestService.sellerResponds(id, response.getItemUpdates(), response.getShippingCost()));
    }

    @PostMapping("/{id}/seller_modifies")
    @Operation(summary = "Seller modifies existing quote pricing")
    public ResponseEntity<QuoteRequest> sellerModifies(@PathVariable UUID id, @Valid @RequestBody com.quorion.b2b.dto.QuoteResponseDTO modification) {
        return ResponseEntity.ok(quoteRequestService.sellerModifies(id, modification.getItemUpdates(), modification.getShippingCost()));
    }

    @PostMapping("/{id}/buyer_responds")
    @Operation(summary = "Buyer counter-offers on quote (re-negotiation)")
    public ResponseEntity<QuoteRequest> buyerResponds(@PathVariable UUID id) {
        return ResponseEntity.ok(quoteRequestService.buyerResponds(id));
    }

    @PostMapping("/{id}/seller_declines")
    @Operation(summary = "Seller declines quote request")
    public ResponseEntity<QuoteRequest> sellerDeclines(@PathVariable UUID id) {
        return ResponseEntity.ok(quoteRequestService.sellerDeclines(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update quote request")
    public ResponseEntity<QuoteRequest> updateQuote(@PathVariable UUID id, @RequestBody QuoteRequest details) {
        QuoteRequest quote = quoteRequestService.findById(id); // Throws EntityNotFoundException if not found
        // Basic update logic - in a real implementation this would update specific fields
        return ResponseEntity.ok(quote);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete quote request")
    public ResponseEntity<Void> deleteQuote(@PathVariable UUID id) {
        quoteRequestService.findById(id); // Throws EntityNotFoundException if not found
        // In a real implementation this would delete the quote
        return ResponseEntity.noContent().build();
    }
}
