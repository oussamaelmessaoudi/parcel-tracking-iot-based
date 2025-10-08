package com.tracksecure.iotgatewayservice.mqtt;


import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class MqttConnectionManager {
    @Value("${mqtt.broker.url}")
    private String brokerUrl;
    @Value("${mqtt.client.id-prefix}")
    private String clientIdPrefix;
    @Value("${mqtt.topic}")
    private String topic;
    @Value("${mqtt.username}")
    private String username;
    @Value("${mqtt.password}")
    private String password;
    private final MqttCallbackHandler mqttCallbackHandler;
    private MqttClient client;

    @PostConstruct
    public void connect(){
        try{
            String clientId = clientIdPrefix+"-"+ UUID.randomUUID();
            client = new MqttClient(brokerUrl, clientId, new MemoryPersistence());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setUserName(username);
            options.setPassword(password.toCharArray());
            options.setAutomaticReconnect(true);
            options.setConnectionTimeout(10);
            options.setKeepAliveInterval(30);

            client.setCallback(mqttCallbackHandler);
            client.connect();
            client.subscribe(topic);

            log.info("connected to broker at {} and subscribed to topic {}", brokerUrl, topic);
        } catch (MqttException e) {
            throw new RuntimeException("MQTT Connection failed",e);
        }
    }

    public MqttClient getMqttClient(){
        return  client;
    }
}
