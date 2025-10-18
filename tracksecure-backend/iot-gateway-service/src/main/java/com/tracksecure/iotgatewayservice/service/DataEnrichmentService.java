package com.tracksecure.iotgatewayservice.service;

import com.tracksecure.iotgatewayservice.model.DeviceMessage;
import com.tracksecure.iotgatewayservice.model.EnrichedEvent;
import com.tracksecure.iotgatewayservice.model.TelemetryPayload;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Map;

@Service
public class DataEnrichmentService {

    public EnrichedEvent enrich(TelemetryPayload payload, DeviceMessage message){
        return EnrichedEvent.builder()
                .eventId(payload.getEventId())
                .deviceId(payload.getDeviceId())
                .shipmentId(payload.getShipmentId())
                .eventType(payload.getEventType())
                .eventTimestamp(toLocalDateTime(payload.getEventTimestamp()))
                .receivedTimestamp(toLocalDateTime(message.getReceivedTimestamp()))
                .latitude(payload.getLatitude() != null ? payload.getLatitude().doubleValue() : null)
                .longitude(payload.getLongitude() != null ? payload.getLongitude().doubleValue() : null)
                .temperature(payload.getTemperature() != null ? payload.getTemperature().doubleValue() : null)
                .humidity(payload.getHumidity() != null ? payload.getHumidity().doubleValue() : null)
                .firmwareVersion(message.getFirmwareVersion())
                .transmissionMethod(message.getTransmissionMethod())
                .metadata(Map.of("source","iot-gateway"))
                .build();
    }

    private LocalDateTime toLocalDateTime(Long epochMillis){
        return Instant.ofEpochSecond(epochMillis).atZone(ZoneOffset.UTC).toLocalDateTime();
    }
}
