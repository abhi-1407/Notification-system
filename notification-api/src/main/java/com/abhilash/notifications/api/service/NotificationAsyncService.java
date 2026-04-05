package com.abhilash.notifications.api.service;

import com.abhilash.notifications.api.channel.EmailChannel;
import com.abhilash.notifications.api.channel.PushChannel;
import com.abhilash.notifications.api.channel.dto.ChannelRequest;
import com.abhilash.notifications.api.entity.Notification;
import com.abhilash.notifications.api.entity.NotificationStatus;
import com.abhilash.notifications.api.entity.NotificationType;
import com.abhilash.notifications.api.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Instant;

import static com.abhilash.notifications.api.entity.NotificationType.PUSH;

@Service
@RequiredArgsConstructor
public class NotificationAsyncService {

    private final NotificationRepository notificationRepository;
    private final AiService aiService;
    private final ChannelSelector selector;
    private final PushChannel pushChannel;
    private final EmailChannel emailChannel;
    private final KafkaDLQProducer dlqProducer;

    @Async
    public void sendNotification(Notification notification) {

        String message = aiService.generateMessage(
                notification.getPayload(),
                notification.getUserId()
        );
        NotificationType type = notification.getType();

        int delay = 1000;
        int maxRetries = 3;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {

                System.out.println("Attempt: " + attempt);
                System.out.println("QALOGS email is " + notification.getEmail());
                ChannelRequest request = ChannelRequest.builder()
                        .userId(notification.getUserId())
                        .message(message)
                        .token(notification.getUserId())
                        .email(notification.getEmail())
                        .build();
                if (type.equals(PUSH)) {
                    pushChannel.send(request);
                } else {
                    emailChannel.send(request);
                }

                // ✅ Update DB
                notification.setStatus(NotificationStatus.SENT);
                notification.setSentAt(Instant.now());
                notification.setMessage(message);
                notification.setEmail(notification.getEmail());
                notificationRepository.save(notification);
                return;

            } catch (Exception e) {

                System.out.println("Retrying...");
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ignored) {}

                delay = Math.min(delay * 2, 8000);
            }
        }

        notification.setStatus(NotificationStatus.FAILED);
        notification.setMessage("FAILED AFTER RETRIES");
        notificationRepository.save(notification);

        dlqProducer.sendToDLQ(notification);
    }
}