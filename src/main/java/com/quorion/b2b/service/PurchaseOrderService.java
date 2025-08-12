package com.quorion.b2b.service;

import com.quorion.b2b.exception.InvalidStateTransitionException;
import com.quorion.b2b.model.commerce.OrderStatus;
import com.quorion.b2b.model.commerce.PurchaseOrder;
import com.quorion.b2b.repository.PurchaseOrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service for PurchaseOrder management with state machine transitions
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;

    public List<PurchaseOrder> findAll() {
        return purchaseOrderRepository.findAll();
    }

    public PurchaseOrder findById(UUID id) {
        return purchaseOrderRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("PurchaseOrder not found with id: " + id));
    }

    /**
     * State Transition: NEW → ACCEPTED
     */
    @Transactional
    public PurchaseOrder accept(UUID orderId) {
        PurchaseOrder order = findById(orderId);
        validateTransition(order.getStatus(), OrderStatus.ACCEPTED);

        order.setStatus(OrderStatus.ACCEPTED);
        log.info("Order {} accepted", orderId);
        return purchaseOrderRepository.save(order);
    }

    /**
     * State Transition: ACCEPTED → IN_PROGRESS
     */
    @Transactional
    public PurchaseOrder makeInProgress(UUID orderId) {
        PurchaseOrder order = findById(orderId);
        validateTransition(order.getStatus(), OrderStatus.IN_PROGRESS);

        order.setStatus(OrderStatus.IN_PROGRESS);
        log.info("Order {} is now in progress", orderId);
        return purchaseOrderRepository.save(order);
    }

    /**
     * State Transition: IN_PROGRESS → INVOICED
     */
    @Transactional
    public PurchaseOrder invoice(UUID orderId) {
        PurchaseOrder order = findById(orderId);
        validateTransition(order.getStatus(), OrderStatus.INVOICED);

        order.setStatus(OrderStatus.INVOICED);
        log.info("Order {} invoiced", orderId);
        return purchaseOrderRepository.save(order);
    }

    /**
     * State Transition: INVOICED → SHIPPED
     */
    @Transactional
    public PurchaseOrder shipOrder(UUID orderId) {
        PurchaseOrder order = findById(orderId);
        validateTransition(order.getStatus(), OrderStatus.SHIPPED);

        order.setStatus(OrderStatus.SHIPPED);
        log.info("Order {} shipped", orderId);
        return purchaseOrderRepository.save(order);
    }

    /**
     * State Transition: SHIPPED → PAYMENT_RECEIVED
     */
    @Transactional
    public PurchaseOrder receivePayment(UUID orderId) {
        PurchaseOrder order = findById(orderId);
        validateTransition(order.getStatus(), OrderStatus.PAYMENT_RECEIVED);

        order.setStatus(OrderStatus.PAYMENT_RECEIVED);
        log.info("Payment received for order {}", orderId);
        return purchaseOrderRepository.save(order);
    }

    /**
     * State Transition: PAYMENT_RECEIVED → COMPLETED
     */
    @Transactional
    public PurchaseOrder complete(UUID orderId) {
        PurchaseOrder order = findById(orderId);
        validateTransition(order.getStatus(), OrderStatus.COMPLETED);

        order.setStatus(OrderStatus.COMPLETED);
        log.info("Order {} completed", orderId);
        return purchaseOrderRepository.save(order);
    }

    /**
     * State Transition: * → CANCELLED
     */
    @Transactional
    public PurchaseOrder cancel(UUID orderId) {
        PurchaseOrder order = findById(orderId);
        order.setStatus(OrderStatus.CANCELLED);
        order.setIsActive(false);
        log.info("Order {} cancelled", orderId);
        return purchaseOrderRepository.save(order);
    }

    private void validateTransition(OrderStatus current, OrderStatus target) {
        boolean valid = switch (current) {
            case NEW -> target == OrderStatus.ACCEPTED;
            case ACCEPTED -> target == OrderStatus.IN_PROGRESS;
            case IN_PROGRESS -> target == OrderStatus.INVOICED;
            case INVOICED -> target == OrderStatus.SHIPPED;
            case SHIPPED -> target == OrderStatus.PAYMENT_RECEIVED;
            case PAYMENT_RECEIVED -> target == OrderStatus.COMPLETED;
            default -> false;
        };

        if (!valid) {
            throw new InvalidStateTransitionException(current.name(), target.name());
        }
    }
}
