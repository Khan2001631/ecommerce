package com.khan.EComm.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "notification_delivery",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"event_id", "recipient_id", "channel"}
                )
        }
)
public class NotificationDelivery {

    @Id
    @GeneratedValue
    private UUID id;

    /**
     * Many delivery attempts belong to one notification event
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private NotificationEvent event;

    @Column(name = "recipient_id", nullable = false)
    private String recipientId;

    @Column(nullable = false)
    private String channel; // EMAIL, IN_APP

    @Column(nullable = false)
    private String status; // PENDING, SENT, FAILED

    @Column(name = "retry_count", nullable = false)
    private int retryCount;

    @Column(name = "last_attempt_at")
    private Instant lastAttemptAt;

    @Column(name = "error_message", length = 1000)
    private String errorMessage;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    // Required by JPA
    protected NotificationDelivery() {
    }

    // Constructor for new deliveries
    public NotificationDelivery(
            NotificationEvent event,
            String recipientId,
            String channel
    ) {
        this.event = event;
        this.recipientId = recipientId;
        this.channel = channel;
        this.status = "PENDING";
        this.retryCount = 0;
        this.createdAt = Instant.now();
    }

    // --------------------
    // Getters
    // --------------------

    public UUID getId() {
        return id;
    }

    public NotificationEvent getEvent() {
        return event;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public String getChannel() {
        return channel;
    }

    public String getStatus() {
        return status;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public Instant getLastAttemptAt() {
        return lastAttemptAt;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    // --------------------
    // Setters (controlled)
    // --------------------

    public void markSent() {
        this.status = "SENT";
        this.lastAttemptAt = Instant.now();
    }

    public void markFailed(String errorMessage) {
        this.status = "FAILED";
        this.retryCount++;
        this.lastAttemptAt = Instant.now();
        this.errorMessage = errorMessage;
    }
}
