package com.abhilash.notifications.api.channel;
import com.abhilash.notifications.api.channel.dto.ChannelRequest;

public interface NotificationChannel {
    void send(ChannelRequest channelRequest);
}
