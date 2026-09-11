package com.khan.EComm.repo;

import com.khan.EComm.model.NotificationDelivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NotificationDeliveryRepository extends JpaRepository<NotificationDelivery, UUID> {
}
