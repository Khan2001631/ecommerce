package com.khan.EComm.events;

import com.khan.EComm.notification.NotificationConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class InMemoryEventPublisher implements EventPublisher {

    private static final Logger logger =
            LoggerFactory.getLogger(InMemoryEventPublisher.class);

    private final NotificationConsumer notificationConsumer;

    public InMemoryEventPublisher(NotificationConsumer notificationConsumer) {
        this.notificationConsumer = notificationConsumer;
    }

    @Override
    public void publish(DomainEvent event) {
        try {
            logger.info(
                    "EVENT PUBLISHED | type={} | eventId={}",
                    event.getEventType(),
                    event.getEventId()
            );

            // Simulate queue delivery
            notificationConsumer.consume(event);

        } catch (Exception ex) {
            // NEVER break business flow
            logger.error(
                    "Failed to deliver event to notification consumer: type={}, eventId={}",
                    event.getEventType(),
                    event.getEventId(),
                    ex
            );
        }
    }
}
