package com.tracksecure.iotgatewayservice.mqtt;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MqttSubscriber {
    private final MqttPahoMessageDrivenChannelAdapter mqttInbound;

    @PostConstruct
    public void init(){
        log.info("MqttSubscriber initiated and listening for messages");
    }

    public void addTopic(String topic, int qos){
        mqttInbound.addTopic(topic, qos);
        log.info("Topic '{}' added to subscriber with Qos '{}' ", topic,qos);
    }

    public void removeTopic(String... topics){
        mqttInbound.removeTopic(topics);
        log.info("Removed subscription from topics: {}", (Object) topics);
    }
}
