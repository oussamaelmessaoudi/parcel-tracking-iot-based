package com.tracksecure.iotgatewayservice.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tracksecure.iotgatewayservice.model.DeviceMessage;
import com.tracksecure.iotgatewayservice.service.MessageTransformService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.transformer.MessageTransformingHandler;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MqttMessageHandler implements MessageHandler {
    private final MessageTransformService messageTransformService;
    private final ObjectMapper objectMapper;

    @Override
    public void handleMessage(Message<?> message) throws MessagingException{
        try{
            String topic = (String) message.getHeaders().get("mqtt_receivedTopic");
            String payload = (String) message.getPayload();

            log.info("Received MQTT message from topic : {}", topic);

            DeviceMessage deviceMessage = parseMessage(payload, topic);

            if(deviceMessage != null && isValidMessage(deviceMessage)){
                messageTransformService.processAndPublish(deviceMessage);
            }else {
                log.warn("Invalid message received from topic : {}", topic);
            }
        }catch (Exception e){
            log.error("Invalid processing MQTT message : {}", e.getMessage(), e);
        }
    }

    private DeviceMessage parseMessage(String payload, String topic){
        try {
            DeviceMessage message = objectMapper.readValue(payload, DeviceMessage.class);
            message.setTopic(topic);
            return message;
        }catch (Exception e){
            log.error("Invalid message received from topic : {}", topic);
            return null;
        }
    }

    private boolean isValidMessage(DeviceMessage message){
        return message.getDeviceId() != null && !message.getDeviceId().isEmpty() && message.getTimestamp() > 0 && message.getLocation() != null;
    }
}
