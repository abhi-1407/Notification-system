package com.abhilash.notifications.api.service;

import com.abhilash.notifications.api.entity.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, Notification> kafkaTemplate;

    private static final String TOPIC = "notifications";

    public void send(Notification notification) {
        kafkaTemplate.send(TOPIC, notification);
    }
}