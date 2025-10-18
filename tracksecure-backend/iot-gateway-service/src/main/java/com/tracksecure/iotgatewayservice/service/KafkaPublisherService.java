package com.tracksecure.iotgatewayservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tracksecure.common.constants.KafkaTopics;
import com.tracksecure.iotgatewayservice.model.EnrichedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaPublisherService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper; // Converter Java Objects -> JSON and vice versa

    public void publish(EnrichedEvent event){
        try{
            String payload = objectMapper.writeValueAsString(event); // Convert the event into a json
            kafkaTemplate.send(KafkaTopics.TRACKING_EVENTS,event.getDeviceId(),payload);
            //Publishes that json into a kafka topic named TRACKING_EVENTS
            //We are using the device id as the message key, so all messages from the same device id will go to the same
            // kafka partition
        }catch (Exception e){
            throw new RuntimeException("Failed to publish event to Kafka",e);
        }
    }
}
