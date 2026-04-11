package com.abhilash.notifications.api.exception;

public class NotificationNotFoundException extends RuntimeException {

    public NotificationNotFoundException(String message) {
        super(message);
    }
}