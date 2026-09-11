package com.khan.EComm.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "dead_letter_notification")
public class DeadLetterNotification {

    @Id
    @GeneratedValue
    private UUID id;

    /**
     * Reference to the delivery attempt that permanently failed
     */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "delivery_id", nullable = false, unique = true)
    private NotificationDelivery delivery;

    @Column(nullable = false, length = 255)
    private String reason;

    @Column(name = "failed_at", nullable = false)
    private Instant failedAt;

    // Required by JPA
    protected DeadLetterNotification() {
    }

    public DeadLetterNotification(NotificationDelivery delivery, String reason) {
        this.delivery = delivery;
        this.reason = reason;
        this.failedAt = Instant.now();
    }

    // --------------------
    // Getters
    // --------------------

    public UUID getId() {
        return id;
    }

    public NotificationDelivery getDelivery() {
        return delivery;
    }

    public String getReason() {
        return reason;
    }

    public Instant getFailedAt() {
        return failedAt;
    }

    // --------------------
    // No setters on purpose
    // --------------------
}
