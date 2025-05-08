package com.quorion.b2b.model.commerce;

/**
 * Order status enumeration
 */
public enum OrderStatus {
    NO_ORDER,
    NEW,
    ACCEPTED,
    IN_PROGRESS,
    INVOICED,
    SHIPPED,
    DELIVERED,
    PAYMENT_RECEIVED,
    COMPLETED,
    CANCELLED,
    DECLINED,
    BACK_ORDERED
}
