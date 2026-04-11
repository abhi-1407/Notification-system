package com.abhilash.notifications.api.controller.dto;

        import com.abhilash.notifications.api.entity.Notification;
        import com.abhilash.notifications.api.entity.NotificationStatus;
        import com.abhilash.notifications.api.entity.NotificationType;
        import lombok.Getter;
        import lombok.NoArgsConstructor;

        import java.time.Instant;
        import java.util.Optional;
        import java.util.UUID;

@Getter
@NoArgsConstructor
public class NotificationResponse {

    private UUID id;
    private String userId;
    private NotificationType type;
    private NotificationStatus status;
    private Instant createdAt;
    private Instant sentAt;
    private String message;
    private String channel;

    public static NotificationResponse from(Notification notification) {
        NotificationResponse res = new NotificationResponse();
        res.id = notification.getId();
        res.userId = notification.getUserId();
        res.type = notification.getType();
        res.status = notification.getStatus();
        res.createdAt = notification.getCreatedAt();
        res.sentAt = notification.getSentAt();
        res.message = notification.getPayload();
        res.channel = notification.getChannel().name();
        return res;
    }
}