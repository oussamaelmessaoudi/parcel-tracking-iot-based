package com.tracksecure.mqttrestapp.service;

import com.tracksecure.mqttrestapp.model.SensorData;
import com.tracksecure.mqttrestapp.repository.SensorDataRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class MongoTestService {

    @Autowired
    private SensorDataRepository sensorDataRepository;

    @PostConstruct
    public void testMongoConnection() {
        try {
            // Create test data
            SensorData testData = SensorData.builder()
                    .deviceId("test-device-001")
                    .receivedAt(LocalDateTime.now())
                    .dhtData(SensorData.DhtData.builder()
                            .temperature(25.5)
                            .humidity(60.2)
                            .timestamp(LocalDateTime.now())
                            .build())
                    .gpsData(SensorData.GpsData.builder()
                            .latitude(35.6895)
                            .longitude(139.6917)
                            .satellites(8)
                            .timestamp(LocalDateTime.now())
                            .build())
                    .build();

            // Save to MongoDB
            SensorData saved = sensorDataRepository.save(testData);
            log.info("✅ MongoDB TEST SUCCESSFUL!");
            log.info("✅ Test data saved with ID: {}", saved.getId());
            log.info("📊 Device: {}, Temp: {}°C, Humidity: {}%", 
                    saved.getDeviceId(),
                    saved.getDhtData().getTemperature(),
                    saved.getDhtData().getHumidity());

        } catch (Exception e) {
            log.error("❌ MongoDB TEST FAILED: {}", e.getMessage());
        }
    }
}