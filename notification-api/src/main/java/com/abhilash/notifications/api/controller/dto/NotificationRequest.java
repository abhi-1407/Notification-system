package com.abhilash.notifications.api.controller.dto;

import com.abhilash.notifications.api.entity.NotificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NotificationRequest {

        @NotBlank(message = "userId is required")
        private String userId;

        @NotNull(message = "type is required")
        private NotificationType type;

        @NotBlank(message = "payload is required")
        private String payload;

        private String email;
        private String phoneNumber;
        private String idempotencyKey;
}