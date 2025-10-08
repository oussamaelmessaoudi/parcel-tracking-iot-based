package com.tracksecure.iotgatewayservice.mqtt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class MqttCallbackHandler implements MqttCallback {
    private final MqttMessageHandler mqttMessageHandler;

    @Override
    public void connectionLost(Throwable cause) {
        log.warn("MQTT Connection lost {}", cause.getMessage());
    }

    @Override
    public void messageArrived(String topic, MqttMessage message){
        log.debug("MQTT Message arrived on topic '{}': {}", topic, message);
        mqttMessageHandler.handle(topic, message);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        log.debug("MQTT delivery complete: {}", token.getMessageId());
    }
}
