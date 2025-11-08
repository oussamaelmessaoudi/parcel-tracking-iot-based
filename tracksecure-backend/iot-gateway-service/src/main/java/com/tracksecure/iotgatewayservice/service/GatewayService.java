package com.tracksecure.iotgatewayservice.service;

import com.tracksecure.iotgatewayservice.model.DeviceMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor
public class GatewayService {
    private final ConcurrentHashMap<String, Instant> deviceLastSeen = new ConcurrentHashMap<>();
    private final AtomicLong messageCounter = new AtomicLong();

    public void recordDeviceActivity(String deviceId){
        deviceLastSeen.put(deviceId, Instant.now());
        long count = messageCounter.incrementAndGet();;

        if(count % 100 == 0){
            log.info("Processed {} messages, Active devices: {}",count,deviceLastSeen.size());
        }
    }

    public Instant getDeviceLastSeen(String deviceId){
        return deviceLastSeen.get(deviceId);
    }

    public boolean isDeviceActive(String deviceId, long maxInteractiveSeconds){
        Instant lastSeen = deviceLastSeen.get(deviceId);
        if(lastSeen == null){
            return false;
        }

        return Instant.now().getEpochSecond() - lastSeen.getEpochSecond() <= maxInteractiveSeconds;
    }

    public long getTotalMessagesProcessed(){
        return messageCounter.get();
    }

    public int getActiveDeviceCount(){
        return deviceLastSeen.size();
    }

    public void validateMessage(DeviceMessage message){
        if(message.getDeviceId() == null || message.getDeviceId().isEmpty()){
            throw new IllegalArgumentException("Device ID is required");
        }

        if(message.getLocation() == null){
            throw new IllegalArgumentException("Device location is required");
        }

        if(message.getLocation().getLatitude() < -90 || message.getLocation().getLatitude() > 90){
            throw new IllegalArgumentException("Device location latitude must be between -90 and 90");
        }

        if(message.getLocation().getLongitude() < -180 || message.getLocation().getLongitude() > 180){
            throw new  IllegalArgumentException("Device location longitude must be between -180 and 180");
        }

        if(message.getTelemetry().getHumidity() < 0 || message.getTelemetry().getHumidity() > 100){
            throw new  IllegalArgumentException("Telemetry humidity must be between 0 and 100");
        }

        if(message.getTelemetry().getTemperature() < -50 || message.getTelemetry().getTemperature() > 100){
            throw new  IllegalArgumentException("Telemetry temperature must be between -50 and 100");
        }
    }
}
