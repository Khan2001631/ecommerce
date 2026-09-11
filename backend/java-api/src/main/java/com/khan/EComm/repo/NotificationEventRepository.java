package com.khan.EComm.repo;

import com.khan.EComm.model.NotificationEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotificationEventRepository extends JpaRepository<NotificationEvent, UUID> {

    Optional<NotificationEvent> findByEventTypeAndEntityId(
            String eventType,
            String entityId
    );
}
