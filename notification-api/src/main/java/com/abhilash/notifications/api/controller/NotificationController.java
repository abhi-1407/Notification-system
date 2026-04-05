package com.abhilash.notifications.api.controller;

import com.abhilash.notifications.api.controller.dto.NotificationRequest;
import com.abhilash.notifications.api.controller.dto.NotificationResponse;
import com.abhilash.notifications.api.entity.Notification;
import com.abhilash.notifications.api.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<NotificationResponse> create(
            @RequestBody @Valid NotificationRequest request) {

        Notification notification = notificationService.create(request);
        System.out.println("QALOGS " + notification);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(NotificationResponse.from(notification));
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getAll() {
        List<NotificationResponse> notifications = notificationService.getAll().stream().map(NotificationResponse::from).toList();
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponse> getById(@PathVariable UUID id) {
        Notification notification = notificationService.getById(id);
        return ResponseEntity.ok(NotificationResponse.from(notification));
    }

    }