package com.tracksecure.iotgatewayservice.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tracksecure.iotgatewayservice.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class MqttMessageHandler{
    private final ObjectMapper objectMapper;
    private final DeviceAuthenticationService deviceAuthenticationService;
    private final PayloadValidationService payloadValidationService;
    private final PayloadDecryptionService payloadDecryptionService;
    private final DataEnrichmentService dataEnrichmentService;
    private final KafkaPublisherService kafkaPublisherService;
    private final IdempotencyService idempotencyService;

    public void handle(String topic, MqttMessage message){
        try {

        }catch (Exception e){

        }
    }
}
