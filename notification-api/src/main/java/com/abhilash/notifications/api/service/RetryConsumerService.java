package com.abhilash.notifications.api.service;

import com.abhilash.notifications.api.entity.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RetryConsumerService {

    private final NotificationAsyncService notificationAsyncService;

    @KafkaListener(topics = "notifications-retry-1", groupId = "retry-1-group")
    public void retry1(Notification notification) throws InterruptedException {
        Thread.sleep(5000); // 5 sec delay

        notificationAsyncService.sendNotification(notification);
    }

    @KafkaListener(topics = "notifications-retry-2", groupId = "retry-2-group")
    public void retry2(Notification notification) throws InterruptedException {
        Thread.sleep(30000); // 30 sec delay
        notificationAsyncService.sendNotification(notification);
    }

    @KafkaListener(topics = "notifications-retry-3", groupId = "retry-3-group")
    public void retry3(Notification notification) throws InterruptedException {
        Thread.sleep(120000); // 2 min delay
        notificationAsyncService.sendNotification(notification);
    }
}
