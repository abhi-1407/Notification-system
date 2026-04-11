package com.abhilash.notifications.api.service;

import com.abhilash.notifications.api.channel.EmailChannel;
import com.abhilash.notifications.api.channel.PushChannel;
import com.abhilash.notifications.api.channel.dto.ChannelRequest;
import com.abhilash.notifications.api.entity.Channel;
import com.abhilash.notifications.api.entity.Notification;
import com.abhilash.notifications.api.entity.NotificationEvent;
import com.abhilash.notifications.api.entity.NotificationStatus;
import com.abhilash.notifications.api.repository.NotificationRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationAsyncService {

    private final NotificationRepository repository;
    private final ObjectMapper objectMapper;
    private final AiService aiService;
    private final PushChannel pushChannel;
    private final EmailChannel emailChannel;
    private final KafkaDLQProducer dlqProducer;

    public void sendNotification(NotificationEvent event) {

        Notification notification = repository.findById(event.getId())
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        log.info("Processing notification id={}", event.getId());

        try {
            processWithRetry(notification);
        } catch (Exception ex) {
            handleFailure(notification, event, ex);
        }
    }

    private void processWithRetry(Notification notification) {

        int maxRetries = 3;
        int delay = 1000;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                log.info("Attempt {} for notification id={}", attempt, notification.getId());

                process(notification);

                markSuccess(notification);
                return;

            } catch (Exception ex) {

                log.error("Attempt {} failed id={}", attempt, notification.getId(), ex);

                sleep(delay);
                delay = Math.min(delay * 2, 8000);
            }
        }

        throw new RuntimeException("All retries failed");
    }

    private void process(Notification notification) throws Exception {

        JsonNode payloadNode = objectMapper.readTree(notification.getPayload());

        String email = getText(payloadNode, "email");
        String phone = getText(payloadNode, "phoneNumber");

        String message = aiService.generateMessage(
                notification.getPayload(),
                notification.getUserId()
        );

        Channel channel = notification.getChannel();

        ChannelRequest request = ChannelRequest.builder()
                .userId(notification.getUserId())
                .message(message)
                .token(notification.getUserId())
                .email(email)
                .phoneNumber(phone)
                .build();

        switch (channel) {
            case PUSH -> pushChannel.send(request);
            case EMAIL -> emailChannel.send(request);
            case SMS -> log.info("Simulating SMS send id={}", notification.getId());
        }

        notification.setMessage(message);
    }

    private void markSuccess(Notification notification) {
        notification.setStatus(NotificationStatus.SENT);
        notification.setSentAt(Instant.now());

        repository.save(notification);

        log.info("Notification sent id={}", notification.getId());
    }

    private void handleFailure(Notification notification, NotificationEvent event, Exception ex) {

        notification.setStatus(NotificationStatus.FAILED);
        repository.save(notification);

        log.error("Notification failed id={}", notification.getId(), ex);

        dlqProducer.sendToDLQ(event);

        log.info("Sent to DLQ id={}", notification.getId());
    }

    private void sleep(int delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException ignored) {}
    }

    private String getText(JsonNode node, String field) {
        return node.has(field) && !node.get(field).isNull()
                ? node.get(field).asText()
                : null;
    }
}