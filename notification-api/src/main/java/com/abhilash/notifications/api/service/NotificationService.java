package com.abhilash.notifications.api.service;

import com.abhilash.notifications.api.controller.dto.NotificationRequest;
import com.abhilash.notifications.api.entity.*;
import com.abhilash.notifications.api.exception.NotificationNotFoundException;
import com.abhilash.notifications.api.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final RateLimiterService rateLimiterService;
    private final ChannelSelectionEngine channelSelectionEngine;
    private final KafkaTemplate kafkaTemplate;
    public List<Notification> getAll() {

        log.info("Fetching all notifications");

        return notificationRepository.findAll();
    }

    public Notification getById(UUID id) {

        log.info("Fetching notification id={}", id);

        return notificationRepository.findById(UUID.fromString(id.toString()))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Notification not found with id=" + id
                ));
    }

    public Notification create(NotificationRequest request) {

        log.info("Incoming create request userId={}, type={}",
                request.getUserId(), request.getType());

        // Rate limiting
        if (!rateLimiterService.isAllowed(request.getUserId())) {
            throw new ResponseStatusException(
                    HttpStatus.TOO_MANY_REQUESTS,
                    "Rate limit exceeded"
            );
        }

        // Idempotency check
        if (request.getIdempotencyKey() != null) {
            Optional<Notification> existing =
                    notificationRepository.findByIdempotencyKey(request.getIdempotencyKey());

            if (existing.isPresent()) {
                log.info("Duplicate request detected, returning existing notification id={}",
                        existing.get().getId());
                return existing.get();
            }
        }

        Channel channel = decideChannelWithFallback(request);
        log.info("Selected channel={} for userId={}", channel, request.getUserId());

        Notification notification = new Notification();
        notification.setUserId(request.getUserId());
        notification.setType(request.getType());
        notification.setPayload(request.getPayload());
        notification.setChannel(channel);
        notification.setStatus(NotificationStatus.PENDING);
        notification.setIdempotencyKey(request.getIdempotencyKey());

        log.info("Processing notification id={}, userId={}, channel={}, type={}",
                notification.getId(),
                notification.getUserId(),
                notification.getChannel(),
                notification.getType());

        Notification saved = notificationRepository.save(notification);

        log.info("Notification created successfully id={}", saved.getId());

        NotificationEvent event = NotificationEvent.from(saved);

        kafkaTemplate.send("notifications", event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish event id={}", saved.getId(), ex);
                    } else {
                        log.info("Published notification event id={}", saved.getId());
                    }
                });

        return saved;
    }

    /**
     * Channel selection with timeout + fallback
     */
    private Channel decideChannelWithFallback(NotificationRequest request) {

        CompletableFuture<Channel> future = CompletableFuture.supplyAsync(() -> {
            log.info("Calling external channel service for userId={}", request.getUserId());
            return channelSelectionEngine.decideChannel(request);
        });

        try {
            // Timeout: 100ms
            return future.get(100, TimeUnit.MILLISECONDS);

        } catch (Exception e) {
            log.warn("Channel service failed or timed out for userId={}, falling back. Error={}",
                    request.getUserId(), e.getMessage());

            return fallbackChannel(request);
        }
    }

    /**
     * Fallback logic (deterministic)
     */
    private Channel fallbackChannel(NotificationRequest request) {
        if (request.getType() == NotificationType.URGENT) {
            return Channel.SMS;
        }
        return Channel.EMAIL;
    }
}