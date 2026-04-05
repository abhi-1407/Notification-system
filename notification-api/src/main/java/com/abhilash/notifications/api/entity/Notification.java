package com.abhilash.notifications.api.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@Table(name = "notifications")
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String userId;

    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(nullable = false)
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status;

    @Column(unique = true)
    private String idempotencyKey;

    private Instant createdAt;
    private Instant sentAt;
    private int retryCount;

    @Column
    private String message;

    /* It validates required fields and automatically sets default values (timestamp + PENDING status) right before the entity is saved to the database. */
    @PrePersist
    void onCreate() {
        if (userId == null || type == null || payload == null) {
            throw new IllegalStateException("Missing required notification fields");
        }
        this.createdAt = Instant.now();
        this.status = NotificationStatus.PENDING;
    }
}