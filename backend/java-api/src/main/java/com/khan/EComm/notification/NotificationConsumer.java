package com.khan.EComm.notification;

import com.khan.EComm.events.DomainEvent;

public interface NotificationConsumer {

    void consume(DomainEvent event);
}
