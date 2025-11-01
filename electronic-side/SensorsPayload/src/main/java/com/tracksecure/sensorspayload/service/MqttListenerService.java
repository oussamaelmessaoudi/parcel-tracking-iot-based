package com.tracksecure.sensorspayload.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tracksecure.sensorspayload.model.DhtData;
import com.tracksecure.sensorspayload.model.GpsData;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class MqttListenerService {

    private final MqttClient mqttClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Getter
    private DhtData latestDhtData;

    @Getter
    private GpsData latestGpsData;

    @PostConstruct
    public void subscribe() throws MqttException {
        mqttClient.subscribe("sensor/dht11", this::handleDht);
        mqttClient.subscribe("sensor/gps", this::handleGps);
        log.info("✅ Subscribed to MQTT topics: sensor/dht11, sensor/gps");
    }

    private void handleDht(String topic, MqttMessage message) {
        try {
            JsonNode json = objectMapper.readTree(message.getPayload());
            double temperature = json.has("temperature") ? json.get("temperature").asDouble() : Double.NaN;
            double humidity = json.has("humidity") ? json.get("humidity").asDouble() : Double.NaN;

            DhtData dhtData = DhtData.builder()
                    .temperature(temperature)
                    .humidity(humidity)
                    .timestamp(LocalDateTime.now())
                    .build();

            latestDhtData = dhtData;
            log.info("🌡️ DHT data received: {}", dhtData);
        } catch (Exception e) {
            log.error("❌ Failed to parse DHT payload", e);
        }
    }

    private void handleGps(String topic, MqttMessage message) {
        try {
            JsonNode json = objectMapper.readTree(message.getPayload());
            double latitude = json.has("latitude") ? json.get("latitude").asDouble() : 0.0;
            double longitude = json.has("longitude") ? json.get("longitude").asDouble() : 0.0;
            int satellites = json.has("satellites") ? json.get("satellites").asInt() : 0;

            GpsData gpsData = GpsData.builder()
                    .latitude(latitude)
                    .longitude(longitude)
                    .satellites(satellites)
                    .timestamp(LocalDateTime.now())
                    .build();

            latestGpsData = gpsData;
            log.info("📡 GPS data received: {}", gpsData);
        } catch (Exception e) {
            log.error("❌ Failed to parse GPS payload", e);
        }
    }
}
