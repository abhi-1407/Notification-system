package com.abhilash.notifications.api.exception.dto;

public record ErrorResponse(
        int status,
        String message,
        long timestamp
) {}