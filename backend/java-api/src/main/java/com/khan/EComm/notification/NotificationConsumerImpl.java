package com.khan.EComm.notification;

import com.khan.EComm.events.DomainEvent;
import com.khan.EComm.events.OrderPlacedEvent;
import com.khan.EComm.events.ProductAddedEvent;
import com.khan.EComm.events.UserRegisteredEvent;
import com.khan.EComm.model.NotificationEvent;
import com.khan.EComm.model.NotificationDelivery;
import com.khan.EComm.repo.NotificationDeliveryRepository;
import com.khan.EComm.repo.NotificationEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationConsumerImpl implements NotificationConsumer {

    private final NotificationEventRepository eventRepository;
    private final NotificationDeliveryRepository deliveryRepository;

    public NotificationConsumerImpl(
            NotificationEventRepository eventRepository,
            NotificationDeliveryRepository deliveryRepository
    ) {
        this.eventRepository = eventRepository;
        this.deliveryRepository = deliveryRepository;
    }

    @Override
    @Transactional
    public void consume(DomainEvent event) {

        switch (event.getEventType()) {

            case "USER_REGISTERED" -> handleUserRegistered(
                    (UserRegisteredEvent) event
            );

            case "ORDER_PLACED" -> handleOrderPlaced(
                    (OrderPlacedEvent) event
            );

            case "PRODUCT_ADDED" -> handleProductAdded(
                    (ProductAddedEvent) event
            );

            default -> throw new IllegalStateException(
                    "Unhandled event type: " + event.getEventType()
            );
        }
    }


    private void createEmailDelivery(
            NotificationEvent event,
            String recipientId
    ) {
        deliveryRepository.save(
                new NotificationDelivery(event, recipientId, "EMAIL")
        );
    }

    private void createAdminDelivery(
            NotificationEvent event,
            String adminId
    ) {
        deliveryRepository.save(
                new NotificationDelivery(event, adminId, "IN_APP")
        );
    }

    private String serialize(DomainEvent event) {
        return event.getData().toString(); // JSON later
    }

    private void handleUserRegistered(UserRegisteredEvent event) {

        UserRegisteredEvent.Data data =
                (UserRegisteredEvent.Data) event.getData();

        String entityId = data.getUserId().toString();

        NotificationEvent notificationEvent =
                eventRepository.findByEventTypeAndEntityId(
                        event.getEventType(),
                        entityId
                ).orElseGet(() ->
                        eventRepository.save(
                                new NotificationEvent(
                                        event.getEventType(),
                                        entityId,
                                        serialize(event)
                                )
                        )
                );

        deliveryRepository.save(
                new NotificationDelivery(
                        notificationEvent,
                        entityId,
                        "EMAIL"
                )
        );
    }


    private void handleOrderPlaced(OrderPlacedEvent event) {

        OrderPlacedEvent.Data data =
                (OrderPlacedEvent.Data) event.getData();

        String entityId = data.getOrderId().toString();
        String recipientId = data.getUserId().toString();

        NotificationEvent notificationEvent =
                eventRepository.findByEventTypeAndEntityId(
                        event.getEventType(),
                        entityId
                ).orElseGet(() ->
                        eventRepository.save(
                                new NotificationEvent(
                                        event.getEventType(),
                                        entityId,
                                        serialize(event)
                                )
                        )
                );

        deliveryRepository.save(
                new NotificationDelivery(
                        notificationEvent,
                        recipientId,
                        "EMAIL"
                )
        );
    }


    private void handleProductAdded(ProductAddedEvent event) {

        ProductAddedEvent.Data data =
                (ProductAddedEvent.Data) event.getData();

        String entityId = data.getProductId().toString();
        String adminId = data.getAdminId().toString();

        NotificationEvent notificationEvent =
                eventRepository.findByEventTypeAndEntityId(
                        event.getEventType(),
                        entityId
                ).orElseGet(() ->
                        eventRepository.save(
                                new NotificationEvent(
                                        event.getEventType(),
                                        entityId,
                                        serialize(event)
                                )
                        )
                );

        deliveryRepository.save(
                new NotificationDelivery(
                        notificationEvent,
                        adminId,
                        "IN_APP"
                )
        );
    }

}
