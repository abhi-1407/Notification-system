package com.abhilash.notifications.api.service;

import com.abhilash.notifications.api.entity.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final NotificationAsyncService asyncService;
    @KafkaListener(topics = "notifications", groupId = "notification-group")
    public void consume(Notification notification) {
        System.out.println("📩 Received from Kafka: " + notification.getId());
        asyncService.sendNotification(notification);
    }
}