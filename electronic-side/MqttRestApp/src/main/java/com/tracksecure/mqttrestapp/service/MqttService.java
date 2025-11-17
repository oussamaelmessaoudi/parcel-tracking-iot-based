package com.tracksecure.mqttrestapp.service;

import com.tracksecure.mqttrestapp.model.SensorData;
import com.tracksecure.mqttrestapp.repository.SensorDataRepository;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
public class MqttService {
    @Getter
    private final AtomicReference<SensorData> latestData = new AtomicReference<>(new SensorData());

    @Autowired
    private SensorDataRepository sensorDataRepository; // Your only addition

    // KEEP ORIGINAL MQTT CONFIG - don't change this!
    private final String brokerUrl = "ssl://192.168.100.253:8883";
    private final String mqttUser = "oussama";
    private final String mqttPassword = "123456";
    private final String clientId = "springbootClient";

    @PostConstruct
    public void initMqtt(){
        try{
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(mqttUser);
            options.setPassword(mqttPassword.toCharArray());
            options.setCleanSession(true);
            options.setSocketFactory(SSLSocketFactoryUtil.getSocketFactory());

            MqttClient client = new MqttClient(brokerUrl, clientId);
            client.connect(options);

            client.subscribe("sensor/dht11", (topic,msg)->{
                String payload = new String(msg.getPayload());
                String[] parts = payload.replaceAll("[{}\"]", "").split(",");
                SensorData data = latestData.get();
                
                for(String part: parts){
                    String[] kv = part.split(":");
                    if(kv[0].trim().equals("temperature")){
                        data.getDhtData().setTemperature(Double.parseDouble(kv[1]));
                    }else if (kv[0].trim().equals("humidity")){
                        data.getDhtData().setHumidity(Double.parseDouble(kv[1]));
                    }
                    data.getDhtData().setTimestamp(LocalDateTime.now());
                }
                latestData.set(data);
                
                // ✅ YOUR CLOUD STORAGE: Save to MongoDB
                saveToMongoDB(data, "dht11");
            });

            client.subscribe("sensor/gps", (topic, msg) -> {
                String payload = new String(msg.getPayload());
                String[] parts = payload.replaceAll("[{}\"]", "").split(",");
                SensorData data = latestData.get();
                
                for (String part : parts) {
                    String[] kv = part.split(":");
                    switch (kv[0].trim()) {
                        case "latitude" -> data.getGpsData().setLatitude(Double.parseDouble(kv[1]));
                        case "longitude" -> data.getGpsData().setLongitude(Double.parseDouble(kv[1]));
                        case "satellites" -> data.getGpsData().setSatellites(Integer.parseInt(kv[1]));
                    }
                    data.getGpsData().setTimestamp(LocalDateTime.now());
                }
                latestData.set(data);
                
                // ✅ YOUR CLOUD STORAGE: Save to MongoDB
                saveToMongoDB(data, "gps");
            });

            log.info("Mqtt connected and subscribed to all topics");
        } catch (Exception e){
            log.error("Error connecting to MQTT broker", e);
            // Even if MQTT fails, MongoDB is ready for when it works
        }
    }

    // ✅ YOUR CLOUD STORAGE METHOD - completely new, doesn't affect existing code
    private void saveToMongoDB(SensorData sensorData, String sensorType) {
        try {
            SensorData dataToSave = SensorData.builder()
                    .deviceId("esp8266-device")
                    .receivedAt(LocalDateTime.now())
                    .build();

            if (sensorData.getDhtData() != null && ("dht11".equals(sensorType) || sensorData.getDhtData().getTemperature() != 0)) {
                dataToSave.setDhtData(SensorData.DhtData.builder()
                        .temperature(sensorData.getDhtData().getTemperature())
                        .humidity(sensorData.getDhtData().getHumidity())
                        .timestamp(sensorData.getDhtData().getTimestamp())
                        .build());
            }

            if (sensorData.getGpsData() != null && ("gps".equals(sensorType) || sensorData.getGpsData().getLatitude() != 0)) {
                dataToSave.setGpsData(SensorData.GpsData.builder()
                        .latitude(sensorData.getGpsData().getLatitude())
                        .longitude(sensorData.getGpsData().getLongitude())
                        .satellites(sensorData.getGpsData().getSatellites())
                        .timestamp(sensorData.getGpsData().getTimestamp())
                        .build());
            }

            SensorData savedData = sensorDataRepository.save(dataToSave);
            log.info("✅ Data saved to MongoDB with ID: {}", savedData.getId());
            
            if (savedData.getDhtData() != null) {
                log.info("📊 Temperature: {}°C, Humidity: {}%", 
                        savedData.getDhtData().getTemperature(), 
                        savedData.getDhtData().getHumidity());
            }
            if (savedData.getGpsData() != null) {
                log.info("📍 Location: Lat {}, Lon {}, Satellites: {}", 
                        savedData.getGpsData().getLatitude(),
                        savedData.getGpsData().getLongitude(),
                        savedData.getGpsData().getSatellites());
            }

        } catch (Exception e) {
            log.error("❌ Error saving data to MongoDB", e);
        }
    }
}