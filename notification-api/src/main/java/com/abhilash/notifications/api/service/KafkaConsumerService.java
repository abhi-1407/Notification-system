package com.abhilash.notifications.api.service;

import com.abhilash.notifications.api.entity.NotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private final NotificationAsyncService asyncService;

    @KafkaListener(topics = "notifications", groupId = "debug-group")
    public void consume(NotificationEvent event) {
        System.out.println("Received from Kafka: " + event.getId());
        asyncService.sendNotification(event);
    }
}