package com.tracksecure.mqttrestapp.service;

import com.tracksecure.mqttrestapp.kafka.producer.KafkaPublisher;
import com.tracksecure.mqttrestapp.model.SensorData;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
@RequiredArgsConstructor
public class MqttService {

    private final KafkaPublisher kafkaPublisher;

    @Getter
    private final AtomicReference<SensorData> latestData = new AtomicReference<>(new SensorData());

    @Value("${mqtt.broker.url}")
    private String brokerUrl;

    @Value("${mqtt.broker.username}")
    private String mqttUser;

    @Value("${mqtt.broker.password}")
    private String mqttPassword;

    @Value("${mqtt.client.id}")
    private String clientId;

    @Value("${kafka.topic.sensor-data}")
    private String sensorDataTopic;

    @PostConstruct
    public void initMqtt() {
        try {
            log.info("Attempting to connect to MQTT broker: {}", brokerUrl);
            
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(mqttUser);
            options.setPassword(mqttPassword.toCharArray());
            options.setCleanSession(true);
            options.setAutomaticReconnect(true);
            options.setConnectionTimeout(30);
            options.setKeepAliveInterval(60);
            
            // REMOVED SSL configuration for plain TCP connection
            // options.setSocketFactory(SSLSocketFactoryUtil.getSocketFactory());
            // options.setSSLHostnameVerifier((hostname, session) -> true);

            MqttClient client = new MqttClient(brokerUrl, clientId);
            client.connect(options);
            
            log.info("✅ Successfully connected to MQTT broker");

            // Subscribe to DHT11 sensor topic
            client.subscribe("sensor/dht11", (topic, msg) -> {
                String payload = new String(msg.getPayload());
                log.info("📥 MQTT message received on '{}': {}", topic, payload);

                try {
                    SensorData data = latestData.get();
                    String[] parts = payload.replaceAll("[{}\"]", "").split(",");

                    for (String part : parts) {
                        String[] kv = part.split(":");
                        if (kv.length == 2) {
                            String key = kv[0].trim();
                            double value = Double.parseDouble(kv[1].trim());

                            if (key.equals("temperature")) {
                                data.getDhtData().setTemperature(value);
                            } else if (key.equals("humidity")) {
                                data.getDhtData().setHumidity(value);
                            }
                        }
                    }
                    data.getDhtData().setTimestamp(LocalDateTime.now());
                    latestData.set(data);

                    log.info("Latest updated DHT Data: {}", data.getDhtData().toString());

                    // Publish to Kafka
                    kafkaPublisher.publish(sensorDataTopic, data);
                    log.info("✅ DHT data published to Kafka topic: {}", sensorDataTopic);

                } catch (Exception e) {
                    log.error("❌ Error processing DHT data: {}", e.getMessage(), e);
                }
            });

            // Subscribe to GPS sensor topic
            client.subscribe("sensor/gps", (topic, msg) -> {
                String payload = new String(msg.getPayload());
                log.info("📥 MQTT message received on '{}': {}", topic, payload);

                try {
                    SensorData data = latestData.get();
                    String[] parts = payload.replaceAll("[{}\"]", "").split(",");

                    for (String part : parts) {
                        String[] kv = part.split(":");
                        if (kv.length == 2) {
                            String key = kv[0].trim();
                            String valueStr = kv[1].trim();

                            switch (key) {
                                case "latitude" -> data.getGpsData().setLatitude(Double.parseDouble(valueStr));
                                case "longitude" -> data.getGpsData().setLongitude(Double.parseDouble(valueStr));
                                case "satellites" -> data.getGpsData().setSatellites(Integer.parseInt(valueStr));
                            }
                        }
                    }
                    data.getGpsData().setTimestamp(LocalDateTime.now());
                    latestData.set(data);

                    log.info("Latest updated GPS data: {}", data.getGpsData().toString());

                    // Publish to Kafka
                    kafkaPublisher.publish(sensorDataTopic, data);
                    log.info("✅ GPS data published to Kafka topic: {}", sensorDataTopic);

                } catch (Exception e) {
                    log.error("❌ Error processing GPS data: {}", e.getMessage(), e);
                }
            });

            log.info("✅ MQTT connected and subscribed to sensor topics");
            log.info("📡 Broker: {}, Client ID: {}", brokerUrl, clientId);

        } catch (Exception e) {
            log.error("❌ Failed to initialize MQTT connection: {}", e.getMessage(), e);
            e.printStackTrace(); // Print full stack trace for debugging
        }
    }
}