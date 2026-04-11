package com.abhilash.notifications.api.service;

import com.abhilash.notifications.api.entity.NotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaDLQProducer {

    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    private static final String DLQ_TOPIC = "notifications-dlq";

    public void sendToDLQ(NotificationEvent event) {
        kafkaTemplate.send(DLQ_TOPIC, event);
    }
}