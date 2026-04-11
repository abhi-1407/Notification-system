package com.abhilash.notifications.api.entity;

import com.abhilash.notifications.api.entity.Notification;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent {

    private UUID id;
    private String userId;
    private String message;
    private String channel;
    private String type;

    public static NotificationEvent from(Notification notification) {
        return NotificationEvent.builder()
                .id(notification.getId())
                .userId(notification.getUserId())
                .message(notification.getMessage())
                .channel(notification.getChannel().name())
                .type(notification.getType().name())
                .build();
    }
}