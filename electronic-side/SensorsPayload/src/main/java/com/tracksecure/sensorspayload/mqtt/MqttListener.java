package com.tracksecure.sensorspayload.mqtt;

import com.tracksecure.sensorspayload.service.PayloadService;
import lombok.RequiredArgsConstructor;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.stereotype.Component;

@Component
public class MqttListener {

    private final PayloadService sensorService;

    public MqttListener(PayloadService sensorService, PayloadService sensorService1) throws MqttException {
        this.sensorService = sensorService1;
        MqttClient client = new MqttClient("tcp://broker.hivemq.com:1883", MqttClient.generateClientId());
        client.connect();
        client.subscribe("sensor/dht11", (topic, message) -> {
            String payload = new String(message.getPayload());
            sensorService.processPayload(payload); // delegate to service
        });
    }
}

