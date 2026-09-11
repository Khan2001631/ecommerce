package com.khan.EComm.events;

public interface EventPublisher {

    void publish(DomainEvent event);
}
