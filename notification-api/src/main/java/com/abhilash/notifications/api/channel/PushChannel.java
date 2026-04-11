package com.abhilash.notifications.api.channel;

import com.abhilash.notifications.api.channel.dto.ChannelRequest;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.stereotype.Service;

@Service
public class PushChannel implements NotificationChannel {

    @Override
    public void send(ChannelRequest request) {

        String token = request.getToken();
        String messageBody = request.getMessage();

        try {
            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(
                            Notification.builder()
                                    .setTitle("Notification")
                                    .setBody(messageBody)
                                    .build()
                    )
                    .build();

            FirebaseMessaging.getInstance().send(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}