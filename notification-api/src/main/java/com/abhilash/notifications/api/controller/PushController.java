package com.abhilash.notifications.api.controller;

import com.abhilash.notifications.api.controller.dto.NotificationRequest;
import com.abhilash.notifications.api.entity.Notification;
import com.abhilash.notifications.api.service.NotificationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notify")
public class PushController {

    private final NotificationService service;

    public PushController(NotificationService service) {
        this.service = service;
    }

    @PostMapping
    public Notification send(@RequestBody NotificationRequest request) {
        return service.create(request);
    }
}