package com.abhilash.notifications.api.channel;

import com.abhilash.notifications.api.channel.dto.ChannelRequest;
import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailChannel implements NotificationChannel {

    @Value("${sendgrid.api.key}")
    private String apiKey;

    @Value("${sendgrid.from.email}")
    private String fromEmail;

    @Override
    public void send(ChannelRequest channelRequest) {

        System.out.println("QALOGS API KEY: " + apiKey);
        System.out.println("QALOGS FROM EMAIL: " + fromEmail);

        String email = channelRequest.getEmail();
        String message = channelRequest.getMessage();

        System.out.println("QALOGS email is " + email);
        System.out.println("QALOGS message is " + message);

        if (email == null || email.isEmpty()) {
            throw new RuntimeException("QALOGS Email is missing");
        }

        Email from = new Email(fromEmail);
        Email to = new Email(email);

        String subject = "Notification";
        Content content = new Content("text/plain", message);

        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(apiKey);
        Request req = new Request();
        System.out.println("QALOGS sg and req done");

        try {
            req.setMethod(Method.POST);
            req.setEndpoint("mail/send");
            req.setBody(mail.build());

            Response response = sg.api(req);

            System.out.println("QALOGS Status: " + response.getStatusCode());
            System.out.println("QALOGS Body: " + response.getBody());
            System.out.println("QALOGS Headers: " + response.getHeaders());

            if (response.getStatusCode() >= 400) {
                throw new RuntimeException("QALOGS SendGrid failed: " + response.getBody());
            }

        } catch (Exception e) {
            throw new RuntimeException("QALOGS Email sending failed", e);
        }
    }
}