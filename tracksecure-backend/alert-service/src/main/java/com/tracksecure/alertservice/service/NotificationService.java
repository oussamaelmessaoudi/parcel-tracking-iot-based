package com.tracksecure.alertservice.service;

import com.tracksecure.alertservice.model.Alert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final JavaMailSender mailSender;

    @Value("${notification.email.from:alerts@tracksecure.com}")
    private String fromEmail;

    @Value("${notification.email.to:admin@tracksecure.com}")
    private String toEmail;

    @Value("${notification.email.enabled:false}")
    private boolean emailEnabled;

    @Async
    public void sendAlertNotification(Alert alert) {
        log.info("Sending notifications for alert: {}", alert.getAlertId());

        // Send email notification
        if (emailEnabled) {
            sendEmailNotification(alert);
        }

        // Send SMS notification (placeholder)
        sendSMSNotification(alert);

        // Send push notification (placeholder)
        sendPushNotification(alert);

        // Send webhook notification (placeholder)
        sendWebhookNotification(alert);
    }

    private void sendEmailNotification(Alert alert) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(String.format("[%s] %s", alert.getSeverity(), alert.getTitle()));
            message.setText(buildEmailBody(alert));

            mailSender.send(message);

            log.info("Email notification sent for alert: {}", alert.getAlertId());

        } catch (Exception e) {
            log.error("Failed to send email notification: {}", e.getMessage(), e);
        }
    }

    private String buildEmailBody(Alert alert) {
        return String.format("""
            Alert Details:
            
            Type: %s
            Severity: %s
            Device ID: %s
            Timestamp: %s
            
            Message:
            %s
            
            Location: %.6f, %.6f
            
            Alert ID: %s
            """,
                alert.getAlertType(),
                alert.getSeverity(),
                alert.getDeviceId(),
                alert.getTimestamp(),
                alert.getMessage(),
                alert.getLatitude() != null ? alert.getLatitude() : 0.0,
                alert.getLongitude() != null ? alert.getLongitude() : 0.0,
                alert.getAlertId()
        );
    }

    private void sendSMSNotification(Alert alert) {
        // Placeholder for SMS notification
        // Integrate with Twilio, AWS SNS, or similar service
        log.debug("SMS notification placeholder for alert: {}", alert.getAlertId());
    }

    private void sendPushNotification(Alert alert) {
        // Placeholder for push notification
        // Integrate with Firebase Cloud Messaging, OneSignal, etc.
        log.debug("Push notification placeholder for alert: {}", alert.getAlertId());
    }

    private void sendWebhookNotification(Alert alert) {
        // Placeholder for webhook notification
        // Send HTTP POST to configured webhook URL
        log.debug("Webhook notification placeholder for alert: {}", alert.getAlertId());
    }
}