package com.tracksecure.mqttrestapp.controller;

import com.tracksecure.mqttrestapp.model.SensorData;
import com.tracksecure.mqttrestapp.service.MqttService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicReference;

@RestController
@RequiredArgsConstructor
public class SensorController {
    private final MqttService mqttService;

    @GetMapping("/sensor-data")
    public AtomicReference<SensorData> getSensorData(){
        return mqttService.getLatestData();
    }
}
