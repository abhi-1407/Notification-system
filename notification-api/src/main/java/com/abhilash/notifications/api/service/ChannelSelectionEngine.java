package com.abhilash.notifications.api.service;

import com.abhilash.notifications.api.controller.dto.NotificationRequest;
import com.abhilash.notifications.api.entity.Channel;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChannelSelectionEngine {

    private final AiService aiService;
    private final ObjectMapper objectMapper;

    public Channel decideChannel(NotificationRequest request) {

        log.info("Deciding channel for userId={}, type={}",
                request.getUserId(), request.getType());

        try {
            JsonNode payloadNode = objectMapper.readTree(request.getPayload());

            String email = getText(payloadNode, "email");
            String phoneNumber = getText(payloadNode, "phoneNumber");

            String result = aiService.decideChannel(
                    request.getType().name(),
                    email,
                    phoneNumber
            );

            log.info("AI response={}", result);

            return mapToChannel(result);

        } catch (Exception e) {
            log.error("AI decision failed, using fallback", e);
            return fallback(request);
        }
    }

    private Channel mapToChannel(String result) {
        if (result == null) return Channel.EMAIL;

        return switch (result.trim().toUpperCase()) {
            case "SMS" -> Channel.SMS;
            case "PUSH" -> Channel.PUSH;
            default -> Channel.EMAIL;
        };
    }

    private Channel fallback(NotificationRequest request) {
        try {
            JsonNode payloadNode = objectMapper.readTree(request.getPayload());

            String phone = getText(payloadNode, "phoneNumber");
            String email = getText(payloadNode, "email");

            if (phone != null && !phone.isBlank()) {
                return Channel.SMS;
            }

            if (email != null && !email.isBlank()) {
                return Channel.EMAIL;
            }

        } catch (Exception ignored) {}

        return Channel.PUSH;
    }

    private String getText(JsonNode node, String field) {
        return node.has(field) && !node.get(field).isNull()
                ? node.get(field).asText()
                : null;
    }
}