package com.abhilash.notifications.api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.*;

@Service
public class AiService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${openai.api.key}")
    private String apiKey;

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    public String generateMessage(String event, String userName) {
        try {
            String prompt = "Generate a short (max 15 words), engaging push notification for event: "
                    + event + " for user " + userName + ". Add emojis if relevant.";

            // Request body
            Map<String, Object> body = new HashMap<>();
            body.put("model", "gpt-4o-mini");

            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "user", "content", prompt));
            body.put("messages", messages);

            // Headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            // API call
            ResponseEntity<Map> response =
                    restTemplate.postForEntity(API_URL, request, Map.class);

            Map responseBody = response.getBody();

            List<Map<String, Object>> choices =
                    (List<Map<String, Object>>) responseBody.get("choices");

            if (choices == null || choices.isEmpty()) {
                return fallback(event, userName);
            }

            Map<String, Object> message =
                    (Map<String, Object>) choices.get(0).get("message");

            return message.get("content").toString().trim();

        } catch (Exception e) {
            e.printStackTrace();
            return fallback(event, userName);
        }
    }

    public String decideChannel(String type, String email, String phoneNumber) {

        try {
            String prompt = """
        You are a notification routing AI.

        Decide the best channel: EMAIL, SMS, or PUSH.

        Rules:
        - URGENT → prefer SMS
        - If phone not available → avoid SMS
        - If email available → prefer EMAIL
        - Otherwise → PUSH

        Input:
        type=%s
        email=%s
        phone=%s

        Output ONLY one word: EMAIL or SMS or PUSH
        """.formatted(type, email, phoneNumber);

            Map<String, Object> body = new HashMap<>();
            body.put("model", "gpt-4o-mini");

            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "user", "content", prompt));
            body.put("messages", messages);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response =
                    restTemplate.postForEntity(API_URL, request, Map.class);

            Map responseBody = response.getBody();

            List<Map<String, Object>> choices =
                    (List<Map<String, Object>>) responseBody.get("choices");

            if (choices == null || choices.isEmpty()) {
                return "EMAIL"; // fallback
            }

            Map<String, Object> message =
                    (Map<String, Object>) choices.get(0).get("message");

            return message.get("content").toString().trim().toUpperCase();

        } catch (Exception e) {
            return "EMAIL"; // safe fallback
        }
    }

    private String fallback(String event, String userName) {
        return "Hey " + userName + ", " + event ;
    }
}