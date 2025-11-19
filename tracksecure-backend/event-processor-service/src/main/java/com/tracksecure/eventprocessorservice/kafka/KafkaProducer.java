package com.tracksecure.eventprocessorservice.kafka;

import com.tracksecure.proto.ProcessedEventProto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    @Value("${kafka.topic.tracking-events}")
    private String processedEventsTopic;

    @Value("${kafka.topic.invalid-events}")
    private String invalidEventsTopic;

    @Value("${kafka.topic.anomaly-events}")
    private String anomalyEventsTopic;

    @Value("${kafka.topic.enriched-events}")
    private String enrichedEventsTopic;

    public void sendProcessedEvent(ProcessedEventProto.ProcessedEvent event) {
        try {
            byte[] eventBytes = event.toByteArray();

            kafkaTemplate.send(processedEventsTopic, event.getDeviceId(), eventBytes)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.debug("Sent processed event {} to topic: {}",
                                    event.getEventId(), processedEventsTopic);
                        } else {
                            log.error("Failed to send processed event: {}", ex.getMessage());
                        }
                    });

        } catch (Exception e) {
            log.error("Error sending processed event: {}", e.getMessage(), e);
        }
    }

    public void sendInvalidEvent(ProcessedEventProto.ProcessedEvent event) {
        try {
            byte[] eventBytes = event.toByteArray();

            kafkaTemplate.send(invalidEventsTopic, event.getDeviceId(), eventBytes)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Sent invalid event {} to dead letter queue",
                                    event.getEventId());
                        } else {
                            log.error("Failed to send invalid event: {}", ex.getMessage());
                        }
                    });

        } catch (Exception e) {
            log.error("Error sending invalid event: {}", e.getMessage(), e);
        }
    }

    public void sendAnomalyEvent(ProcessedEventProto.ProcessedEvent event) {
        try {
            byte[] eventBytes = event.toByteArray();

            kafkaTemplate.send(anomalyEventsTopic, event.getDeviceId(), eventBytes)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.warn("Sent anomaly event {} for device: {}",
                                    event.getEventId(), event.getDeviceId());
                        } else {
                            log.error("Failed to send anomaly event: {}", ex.getMessage());
                        }
                    });

        } catch (Exception e) {
            log.error("Error sending anomaly event: {}", e.getMessage(), e);
        }
    }

    public void sendEnrichedEvent(ProcessedEventProto.ProcessedEvent event) {
        try {
            byte[] eventBytes = event.toByteArray();

            kafkaTemplate.send(enrichedEventsTopic, event.getDeviceId(), eventBytes)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.debug("Sent enriched event {} to topic: {}",
                                    event.getEventId(), enrichedEventsTopic);
                        } else {
                            log.error("Failed to send enriched event: {}", ex.getMessage());
                        }
                    });

        } catch (Exception e) {
            log.error("Error sending enriched event: {}", e.getMessage(), e);
        }
    }
}

