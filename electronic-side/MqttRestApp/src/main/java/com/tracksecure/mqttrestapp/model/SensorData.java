package com.tracksecure.mqttrestapp.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "sensor_data")  // MongoDB annotation
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SensorData {
    
    @Id  // MongoDB ID field
    private String id;
    
    private String deviceId;  // Add device identifier
    private DhtData dhtData;
    private GpsData gpsData;
    private LocalDateTime receivedAt;  // When we received the data

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DhtData {
        private double temperature;
        private double humidity;
        private LocalDateTime timestamp;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GpsData {
        private double longitude;
        private double latitude;
        private int satellites;
        private LocalDateTime timestamp;
    }
}