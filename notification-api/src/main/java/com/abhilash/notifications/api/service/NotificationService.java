package com.abhilash.notifications.api.service;

import com.abhilash.notifications.api.controller.dto.NotificationRequest;
import com.abhilash.notifications.api.entity.Notification;
import com.abhilash.notifications.api.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final KafkaProducerService kafkaProducerService;
    private final RateLimiterService rateLimiterService;
    public Notification create(NotificationRequest notificationRequest) {

        if (!rateLimiterService.isAllowed(notificationRequest.getUserId())) {
            throw new ResponseStatusException(
                    HttpStatus.TOO_MANY_REQUESTS,
                    "Rate limit exceeded"
            );
        }

        if (notificationRequest.getIdempotencyKey() != null) {
            Optional<Notification> existing = notificationRepository.findByIdempotencyKey(notificationRequest.getIdempotencyKey());
            if (existing.isPresent()) {
                return existing.get();
            }
        }

        Notification notification = notificationRepository.save(
                Notification.builder()
                        .userId(notificationRequest.getUserId())
                        .type(notificationRequest.getType())
                        .payload(notificationRequest.getPayload())
                        .email(notificationRequest.getEmail())
                        .idempotencyKey(notificationRequest.getIdempotencyKey())
                        .build()
        );
        kafkaProducerService.send(notification);
        return notification;
    }

    public List<Notification> getAll() {
        return notificationRepository.findAll();
    }

    public Notification getById(UUID id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Notification not found"
                ));
    }
}