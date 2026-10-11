package com.freshsupply.fms.domain;

/**
 * Order lifecycle (State pattern, enum flavour).
 * PENDING -> CONFIRMED -> SHIPPED -> DELIVERED, and PENDING/CONFIRMED -> CANCELLED.
 */
public enum OrderStatus {
    PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED;

    public boolean canTransitionTo(OrderStatus next) {
        return switch (this) {
            case PENDING -> next == CONFIRMED || next == CANCELLED;
            case CONFIRMED -> next == SHIPPED || next == CANCELLED;
            case SHIPPED -> next == DELIVERED;
            case DELIVERED, CANCELLED -> false;
        };
    }
}
