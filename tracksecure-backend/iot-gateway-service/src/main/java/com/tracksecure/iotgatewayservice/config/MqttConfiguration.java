package com.tracksecure.iotgatewayservice.config;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
public class MqttConfiguration {
    @Value("${mqtt.broker.url}")
    private String brokerUrl;
    @Value("${mqtt.client.id-prefix}")
    private String clientIdPrefix;
    @Value("${mqtt.username}")
    private String username;
    @Value("${mqtt.password}")
    private String password;

    @Bean
    public MqttClient mqttClient() throws MqttException{
        String clientId = clientIdPrefix + "-" + UUID.randomUUID();
        MqttClient mqttClient = new MqttClient(brokerUrl, clientId, new MemoryPersistence());
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        options.setUserName(username);
        options.setPassword(password.toCharArray());
        options.setAutomaticReconnect(true);
        options.setConnectionTimeout(10);
        options.setKeepAliveInterval(30);
        mqttClient.connect(options);
        return mqttClient;
    }
}
