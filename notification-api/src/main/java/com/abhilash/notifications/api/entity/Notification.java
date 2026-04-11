package com.abhilash.notifications.api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String userId;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    private Channel channel;

    private String payload;
    private String message;

    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    private String idempotencyKey;

    private Instant createdAt;
    private Instant sentAt;
    @PrePersist
    public void onCreate() {
        this.createdAt = Instant.now();

        if (userId == null) {
            throw new IllegalStateException("userId is missing");
        }
        if (type == null) {
            throw new IllegalStateException("type is missing");
        }
        if (payload == null) {
            throw new IllegalStateException("payload is missing");
        }
        if (channel == null) {
            throw new IllegalStateException("channel is missing");
        }
    }
}