package com.tracksecure.eventprocessorservice.consumer;

import com.google.protobuf.InvalidProtocolBufferException;
import com.tracksecure.eventprocessorservice.service.EventProcessingService;
import com.tracksecure.proto.TrackingEventProto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaEventConsumer {

    private final EventProcessingService eventProcessingService;

    @KafkaListener(
            topics = "${kafka.topic.tracking-events}",
            groupId = "${spring.kafka.consumer.group-id}",
            concurrency = "3"
    )
    public void consumeTrackingEvent(byte[] message) {
        try {
            TrackingEventProto.TrackingEvent event =
                    TrackingEventProto.TrackingEvent.parseFrom(message);

            log.debug("Consumed tracking event: {} from device: {}",
                    event.getEventId(), event.getDeviceId());

            eventProcessingService.processEvent(event);

        } catch (InvalidProtocolBufferException e) {
            log.error("Failed to parse tracking event: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error consuming tracking event: {}", e.getMessage(), e);
        }
    }
}
