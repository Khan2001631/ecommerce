package com.khan.EComm.events;

import java.time.Instant;
import java.util.UUID;

public class UserRegisteredEvent implements DomainEvent {

    private final UUID eventId;
    private final Instant occurredAt;
    private final Data data;

    public UserRegisteredEvent(Long userId, String email) {
        this.eventId = UUID.randomUUID();
        this.occurredAt = Instant.now();
        this.data = new Data(userId, email);
    }

    @Override
    public UUID getEventId() {
        return eventId;
    }

    @Override
    public String getEventType() {
        return "USER_REGISTERED";
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
        private final Long userId;
        private final String email;

        public Data(Long userId, String email) {
            this.userId = userId;
            this.email = email;
        }

        public Long getUserId() {
            return userId;
        }

        public String getEmail() {
            return email;
        }
    }
}
