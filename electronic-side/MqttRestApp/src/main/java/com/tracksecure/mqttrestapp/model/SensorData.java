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
        @Override
        public  String toString() {
            return "DhtData{" +
                    "temperature=" + temperature +
                    ", humidity=" + humidity +
                    ", timestamp=" + timestamp +
                    '}';
        }
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
        @Override
        public  String toString() {
            return "GpsData{" +
                    "temperature=" + longitude +
                    ", humidity=" + latitude +
                    ", satellites=" + satellites +
                    ", timestamp=" + timestamp +
                    '}';
        }
    }
}
