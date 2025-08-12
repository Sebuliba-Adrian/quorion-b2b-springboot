package com.quorion.b2b.service;

import com.quorion.b2b.exception.InvalidStateTransitionException;
import com.quorion.b2b.model.commerce.*;
import com.quorion.b2b.repository.PurchaseOrderRepository;
import com.quorion.b2b.repository.QuoteRequestRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Service for QuoteRequest management with state machine transitions
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class QuoteRequestService {

    private final QuoteRequestRepository quoteRequestRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;

    public List<QuoteRequest> findAll() {
        return quoteRequestRepository.findAll();
    }

    public QuoteRequest findById(UUID id) {
        return quoteRequestRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("QuoteRequest not found with id: " + id));
    }

    @Transactional
    public QuoteRequest create(QuoteRequest quoteRequest) {
        quoteRequest.setStatus(QuoteStatus.NO_REQUEST);
        quoteRequest.setNumber(generateQuoteNumber());
        return quoteRequestRepository.save(quoteRequest);
    }

    /**
     * State Transition: NO_REQUEST → NEW
     */
    @Transactional
    public QuoteRequest createQuote(UUID quoteId) {
        QuoteRequest quote = findById(quoteId);
        validateTransition(quote.getStatus(), QuoteStatus.NEW);

        quote.setStatus(QuoteStatus.NEW);
        quote.setIsActive(true);
        log.info("Quote {} created", quoteId);
        return quoteRequestRepository.save(quote);
    }

    /**
     * State Transition: NEW → REQUESTED
     */
    @Transactional
    public QuoteRequest buyerRequests(UUID quoteId) {
        QuoteRequest quote = findById(quoteId);
        validateTransition(quote.getStatus(), QuoteStatus.REQUESTED);

        quote.setStatus(QuoteStatus.REQUESTED);
        log.info("Quote {} requested by buyer", quoteId);
        return quoteRequestRepository.save(quote);
    }

    /**
     * State Transition: REQUESTED → RESPONDED
     */
    @Transactional
    public QuoteRequest sellerResponds(UUID quoteId, List<QuoteRequestDetail> itemUpdates, BigDecimal shippingCost) {
        QuoteRequest quote = findById(quoteId);
        validateTransition(quote.getStatus(), QuoteStatus.RESPONDED);

        // Update item prices
        for (QuoteRequestDetail update : itemUpdates) {
            quote.getItems().stream()
                .filter(item -> item.getId().equals(update.getId()))
                .findFirst()
                .ifPresent(item -> item.setPricePerUnit(update.getPricePerUnit()));
        }

        if (shippingCost != null) {
            quote.setShippingCost(shippingCost);
        }

        quote.setStatus(QuoteStatus.RESPONDED);
        log.info("Quote {} responded by seller", quoteId);
        return quoteRequestRepository.save(quote);
    }

    /**
     * State Transition: RESPONDED → REQUESTED (re-negotiation)
     */
    @Transactional
    public QuoteRequest buyerResponds(UUID quoteId) {
        QuoteRequest quote = findById(quoteId);
        validateTransition(quote.getStatus(), QuoteStatus.REQUESTED);

        quote.setStatus(QuoteStatus.REQUESTED);
        log.info("Quote {} re-negotiation requested by buyer", quoteId);
        return quoteRequestRepository.save(quote);
    }

    /**
     * State Transition: RESPONDED → ACCEPTED
     * Creates a PurchaseOrder automatically
     */
    @Transactional
    public QuoteRequest buyerAccepts(UUID quoteId) {
        QuoteRequest quote = findById(quoteId);
        validateTransition(quote.getStatus(), QuoteStatus.ACCEPTED);

        quote.setStatus(QuoteStatus.ACCEPTED);
        quote.setIsActive(false);

        // Create Purchase Order from Quote
        PurchaseOrder order = createOrderFromQuote(quote);
        purchaseOrderRepository.save(order);

        log.info("Quote {} accepted, created order {}", quoteId, order.getId());
        return quoteRequestRepository.save(quote);
    }

    /**
     * State Transition: RESPONDED → RESPONDED (seller modifies pricing)
     */
    @Transactional
    public QuoteRequest sellerModifies(UUID quoteId, List<QuoteRequestDetail> itemUpdates, BigDecimal shippingCost) {
        QuoteRequest quote = findById(quoteId);

        if (quote.getStatus() != QuoteStatus.RESPONDED) {
            throw new InvalidStateTransitionException(quote.getStatus().name(), "MODIFY");
        }

        // Update item prices
        for (QuoteRequestDetail update : itemUpdates) {
            quote.getItems().stream()
                .filter(item -> item.getId().equals(update.getId()))
                .findFirst()
                .ifPresent(item -> item.setPricePerUnit(update.getPricePerUnit()));
        }

        if (shippingCost != null) {
            quote.setShippingCost(shippingCost);
        }

        log.info("Quote {} modified by seller", quoteId);
        return quoteRequestRepository.save(quote);
    }

    /**
     * State Transition: * → DECLINED
     */
    @Transactional
    public QuoteRequest sellerDeclines(UUID quoteId) {
        QuoteRequest quote = findById(quoteId);
        quote.setStatus(QuoteStatus.DECLINED);
        quote.setIsActive(false);
        log.info("Quote {} declined by seller", quoteId);
        return quoteRequestRepository.save(quote);
    }

    /**
     * State Transition: * → CANCELLED
     */
    @Transactional
    public QuoteRequest cancel(UUID quoteId) {
        QuoteRequest quote = findById(quoteId);
        quote.setStatus(QuoteStatus.CANCELLED);
        quote.setIsActive(false);
        log.info("Quote {} cancelled", quoteId);
        return quoteRequestRepository.save(quote);
    }

    /**
     * Create PurchaseOrder from accepted QuoteRequest
     */
    private PurchaseOrder createOrderFromQuote(QuoteRequest quote) {
        PurchaseOrder order = PurchaseOrder.builder()
            .buyer(quote.getBuyer())
            .seller(quote.getSeller())
            .quoteRequest(quote)
            .number(generateOrderNumber())
            .status(OrderStatus.NEW)
            .warehouse(quote.getWarehouse())
            .deliveryTerm(quote.getDeliveryTerm())
            .paymentTerm(quote.getPaymentTerm())
            .paymentMode(quote.getPaymentMode())
            .shippingCost(quote.getShippingCost())
            .currency(quote.getCurrency())
            .isActive(true)
            .build();

        // Copy quote items to order items
        for (QuoteRequestDetail quoteItem : quote.getItems()) {
            PurchaseOrderDetail orderItem = PurchaseOrderDetail.builder()
                .order(order)
                .product(quoteItem.getProduct())
                .sku(quoteItem.getSku())
                .noOfUnits(quoteItem.getNoOfUnits())
                .totalQuantity(quoteItem.getTotalQuantity())
                .pricePerUnit(quoteItem.getPricePerUnit())
                .currency(quoteItem.getCurrency())
                .build();
            order.getItems().add(orderItem);
        }

        return order;
    }

    private void validateTransition(QuoteStatus current, QuoteStatus target) {
        boolean valid = switch (current) {
            case NO_REQUEST -> target == QuoteStatus.NEW;
            case NEW -> target == QuoteStatus.REQUESTED;
            case REQUESTED -> target == QuoteStatus.RESPONDED;
            case RESPONDED -> target == QuoteStatus.ACCEPTED || target == QuoteStatus.REQUESTED;
            default -> false;
        };

        if (!valid) {
            throw new InvalidStateTransitionException(current.name(), target.name());
        }
    }

    private String generateQuoteNumber() {
        return "QT-" + System.currentTimeMillis();
    }

    private String generateOrderNumber() {
        return "PO-" + System.currentTimeMillis();
    }
}
