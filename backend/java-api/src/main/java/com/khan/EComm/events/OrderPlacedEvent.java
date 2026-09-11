package com.khan.EComm.events;

import java.time.Instant;
import java.util.UUID;

public class OrderPlacedEvent implements DomainEvent {

    private final UUID eventId;
    private final Instant occurredAt;
    private final Data data;

    public OrderPlacedEvent(Long orderId, Long userId, double orderTotal) {
        this.eventId = UUID.randomUUID();
        this.occurredAt = Instant.now();
        this.data = new Data(orderId, userId, orderTotal);
    }

    @Override
    public UUID getEventId() {
        return eventId;
    }

    @Override
    public String getEventType() {
        return "ORDER_PLACED";
    }

    @Override
    public Instant getOccurredAt() {
        return occurredAt;
    }

    @Override
    public Object getData() {
        return data;
    }

    public static class Data {
        private final Long orderId;
        private final Long userId;
        private final double orderTotal;

        public Data(Long orderId, Long userId, double orderTotal) {
            this.orderId = orderId;
            this.userId = userId;
            this.orderTotal = orderTotal;
        }

        public Long getOrderId() {
            return orderId;
        }

        public Long getUserId() {
            return userId;
        }

        public double getOrderTotal() {
            return orderTotal;
        }
    }
}
