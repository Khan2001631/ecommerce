package com.khan.EComm.events;

import java.time.Instant;
import java.util.UUID;

public class ProductAddedEvent implements DomainEvent {

    private final UUID eventId;
    private final Instant occurredAt;
    private final Data data;

    public ProductAddedEvent(Long productId, Long adminId) {
        this.eventId = UUID.randomUUID();
        this.occurredAt = Instant.now();
        this.data = new Data(productId, adminId);
    }

    @Override
    public UUID getEventId() {
        return eventId;
    }

    @Override
    public String getEventType() {
        return "PRODUCT_ADDED";
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
        private final Long productId;
        private final Long adminId;

        public Data(Long productId, Long adminId) {
            this.productId = productId;
            this.adminId = adminId;
        }

        public Long getProductId() {
            return productId;
        }

        public Long getAdminId() {
            return adminId;
        }
    }
}
