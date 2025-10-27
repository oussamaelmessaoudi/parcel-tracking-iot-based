package com.tracksecure.mqttrestapp.service;

import com.tracksecure.mqttrestapp.model.SensorData;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLSocketFactory;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
public class MqttService {
    @Getter
    private final AtomicReference<SensorData> latestData = new AtomicReference<>(new SensorData());

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
            });

            log.info("Mqtt connected and subscribed to all topics");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
