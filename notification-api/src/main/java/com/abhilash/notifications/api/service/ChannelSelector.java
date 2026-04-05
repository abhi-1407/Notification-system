package com.abhilash.notifications.api.service;

import org.springframework.stereotype.Service;

@Service
public class ChannelSelector {

    public String selectChannel(String payload) {

        String p = payload.toLowerCase();

        if(p.contains("otp") || p.contains("urgent")) {
            return "PUSH";
        }

        if(p.contains("invoice") || p.contains("report")) {
            return "EMAIL";
        }

        return "PUSH"; // default
    }
}