package com.tracksecure.mqttrestapp.kafka.producer;

import com.tracksecure.mqttrestapp.model.SensorData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaPublisher {
    private final KafkaTemplate<String, SensorData> kafkaTemplate;


    //The kafkaTemplate sends the sensors data into the topic
    public void publish(String topic, SensorData sensorData) {
        kafkaTemplate.send(topic, sensorData);
        log.info("Data {} published to topic {}", sensorData,topic);
    }
}
