package com.tracksecure.mqttrestapp.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import com.tracksecure.mqttrestapp.model.SensorData;
import com.tracksecure.mqttrestapp.service.MqttService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Allow CORS for testing
public class SensorController {

    private final MqttService mqttService;

    @GetMapping("/health")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Service is running");
    }

    @GetMapping("/sensor/latest")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<SensorData> getLatestSensorData() {
        SensorData data = mqttService.getLatestData().get();
        log.info("REST API: Returning latest sensor data");
        return ResponseEntity.ok(data);
    }

    @GetMapping("/sensor/dht")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> getDhtData() {
        SensorData data = mqttService.getLatestData().get();
        return ResponseEntity.ok(data.getDhtData());
    }

    @GetMapping("/sensor/gps")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<?> getGpsData() {
        SensorData data = mqttService.getLatestData().get();
        return ResponseEntity.ok(data.getGpsData());
    }
}