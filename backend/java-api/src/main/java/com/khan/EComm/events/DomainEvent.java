package com.khan.EComm.events;

import java.time.Instant;
import java.util.UUID;

public interface DomainEvent {

    UUID getEventId();

    String getEventType();

    Instant getOccurredAt();

    Object getData();
}
