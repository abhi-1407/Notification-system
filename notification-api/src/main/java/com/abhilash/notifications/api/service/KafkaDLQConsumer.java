package com.abhilash.notifications.api.service;

import com.abhilash.notifications.api.entity.Notification;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaDLQConsumer {

    @KafkaListener(topics = "notifications-dlq", groupId = "dlq-group")
    public void consume(Notification notification) {
        System.out.println("DLQ MESSAGE: " + notification.getId());
    }
}