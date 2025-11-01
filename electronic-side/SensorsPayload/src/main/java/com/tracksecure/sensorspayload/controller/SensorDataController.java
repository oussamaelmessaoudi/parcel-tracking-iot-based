package com.tracksecure.sensorspayload.controller;

import com.tracksecure.sensorspayload.model.DhtData;
import com.tracksecure.sensorspayload.model.GpsData;
import com.tracksecure.sensorspayload.service.MqttListenerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sensors")
@RequiredArgsConstructor
public class SensorDataController {

    private final MqttListenerService mqttListenerService;

    @GetMapping("/dht")
    public DhtData getLatestDhtData() {
        return mqttListenerService.getLatestDhtData();
    }

    @GetMapping("/gps")
    public GpsData getLatestGpsData() {
        return mqttListenerService.getLatestGpsData();
    }
}
