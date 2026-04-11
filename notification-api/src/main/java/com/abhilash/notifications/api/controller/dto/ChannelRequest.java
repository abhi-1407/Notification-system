package com.abhilash.notifications.api.channel.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChannelRequest {

    private String userId;
    private String message;

    private String token;
    private String email;
    private String phoneNumber;
}