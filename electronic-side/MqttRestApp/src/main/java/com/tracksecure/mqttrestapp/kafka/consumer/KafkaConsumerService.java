package com.tracksecure.mqttrestapp.kafka.consumer;

import com.tracksecure.mqttrestapp.model.SensorData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaConsumerService {

    @KafkaListener(topics = "${kafka.topic.sensor-data}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(SensorData sensorData) {
        log.info("📥 Kafka Consumer received SensorData");
        
        try {
            // Log DHT data
            if (sensorData.getDhtData() != null) {
                log.info("🌡️  Temperature: {}°C, Humidity: {}%", 
                    sensorData.getDhtData().getTemperature(),
                    sensorData.getDhtData().getHumidity());
            }
            
            // Log GPS data
            if (sensorData.getGpsData() != null) {
                log.info("📍 GPS: Lat={}, Lon={}, Satellites={}",
                    sensorData.getGpsData().getLatitude(),
                    sensorData.getGpsData().getLongitude(),
                    sensorData.getGpsData().getSatellites());
            }
            
            // TODO: Save to database
            // sensorRepository.save(sensorData);
            
        } catch (Exception e) {
            log.error("❌ Error processing sensor data: {}", e.getMessage(), e);
        }
    }
}