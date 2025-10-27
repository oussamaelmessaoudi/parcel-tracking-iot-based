package com.tracksecure.mqttrestapp.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SensorData {
    private DhtData dhtData = new DhtData();
    private GpsData gpsData = new GpsData();

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DhtData{
        private double temperature;
        private double humidity;
        private LocalDateTime timestamp;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GpsData{
        private double longitude;
        private double latitude;
        private int satellites;
        private LocalDateTime timestamp;
    }
}
