package com.tracksecure.alertservice.kafka;

import com.google.protobuf.InvalidProtocolBufferException;
import com.tracksecure.alertservice.engine.AlertEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaConsumer {

    private final AlertEngine alertEngine;

    @KafkaListener(
            topics = "${kafka.topic.processed-events}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeProcessedEvent(byte[] message) {
        try {
            ProcessedEventProto.ProcessedEvent event =
                    ProcessedEventProto.ProcessedEvent.parseFrom(message);

            log.debug("Received processed event for device: {}", event.getDeviceId());

            // Extract data for alert processing
            String deviceId = event.getDeviceId();
            double latitude = event.getLocation().getLatitude();
            double longitude = event.getLocation().getLongitude();
            Float speed = event.getLocation().getSpeed();
            Float batteryLevel = event.getTelemetry().getBatteryLevel();
            Float temperature = event.getTelemetry().getTemperature();
            Float humidity = event.getTelemetry().getHumidity();

            // Process through alert engine
            alertEngine.processLocationUpdate(
                    deviceId, latitude, longitude, temperature, humidity
            );

        } catch (InvalidProtocolBufferException e) {
            log.error("Failed to parse processed event: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error processing event: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(
            topics = "${kafka.topic.anomaly-events}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeAnomalyEvent(byte[] message) {
        try {
            ProcessedEventProto.ProcessedEvent event =
                    ProcessedEventProto.ProcessedEvent.parseFrom(message);

            log.warn("Received anomaly event for device: {}", event.getDeviceId());

            // Create anomaly alert
            com.tracksecure.alert.model.Alert alert = com.tracksecure.alert.model.Alert.builder()
                    .alertId(java.util.UUID.randomUUID().toString())
                    .deviceId(event.getDeviceId())
                    .alertType(com.tracksecure.alert.model.Alert.AlertType.ANOMALY_DETECTED)
                    .severity(com.tracksecure.alert.model.Alert.AlertSeverity.WARNING)
                    .title("Anomaly Detected")
                    .message(buildAnomalyMessage(event))
                    .latitude(event.getLocation().getLatitude())
                    .longitude(event.getLocation().getLongitude())
                    .timestamp(java.time.Instant.ofEpochMilli(event.getTimestamp()))
                    .build();

            // Use AlertService to create alert
            // Note: You'll need to inject AlertService here
            log.info("Created anomaly alert for device: {}", event.getDeviceId());

        } catch (Exception e) {
            log.error("Error processing anomaly event: {}", e.getMessage(), e);
        }
    }

    private String buildAnomalyMessage(ProcessedEventProto.ProcessedEvent event) {
        StringBuilder message = new StringBuilder("Anomaly detected for device ");
        message.append(event.getDeviceId());

        if (event.getEnrichedData().getAnomalyReasonsCount() > 0) {
            message.append(": ");
            message.append(String.join(", ", event.getEnrichedData().getAnomalyReasonsList()));
        }

        return message.toString();
    }
}
