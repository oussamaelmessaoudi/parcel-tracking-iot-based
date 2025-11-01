package com.tracksecure.mqttrestapp.kafka.consumer;

import com.tracksecure.mqttrestapp.model.SensorData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaConsumerService {
    @KafkaListener(topics = "sensor-data", groupId = "tracking-group")
    public void consume(SensorData sensorData) {
        log.info("Received SensorData: {}", sensorData);
    }
}
