package com.abhilash.notifications.api.service;

import com.abhilash.notifications.api.entity.Notification;
import com.abhilash.notifications.api.entity.NotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RetryConsumerService {

    private final NotificationAsyncService notificationAsyncService;

    @KafkaListener(topics = "notifications-retry-1", groupId = "retry-1-group")
    public void retry1(NotificationEvent event) throws InterruptedException {
        Thread.sleep(5000);
        notificationAsyncService.sendNotification(event);
    }

    @KafkaListener(topics = "notifications-retry-2", groupId = "retry-1-group")
    public void retry2(NotificationEvent event) throws InterruptedException {
        Thread.sleep(5000);
        notificationAsyncService.sendNotification(event);
    }

    @KafkaListener(topics = "notifications-retry-3", groupId = "retry-1-group")
    public void retry3(NotificationEvent event) throws InterruptedException {
        Thread.sleep(5000);
        notificationAsyncService.sendNotification(event);
    }
}
