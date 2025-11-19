package com.tracksecure.eventprocessorservice.service;


import com.tracksecure.eventprocessorservice.model.EventMetadata;
import com.tracksecure.eventprocessorservice.model.ProcessedEvent;
import com.tracksecure.proto.ProcessedEventProto;
import com.tracksecure.proto.TrackingEventProto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class EventEnrichmentService {

    private final Map<String, EventMetadata> deviceMetadataCache = new ConcurrentHashMap<>();

    private static final float STATIONARY_SPEED_THRESHOLD = 1.0f; // km/h
    private static final float MOVING_SPEED_THRESHOLD = 5.0f; // km/h
    private static final long STATIONARY_TIME_THRESHOLD = 300000; // 5 minutes in ms

    public ProcessedEvent.EnrichmentInfo enrich(TrackingEventProto.TrackingEvent event) {
        String deviceId = event.getDeviceId();
        EventMetadata metadata = deviceMetadataCache.get(deviceId);

        ProcessedEvent.EnrichmentInfo.EnrichmentInfoBuilder enrichmentBuilder =
                ProcessedEvent.EnrichmentInfo.builder();

        // Calculate distance and time since last event
        if (metadata != null && metadata.getLastLatitude() != null && metadata.getLastLongitude() != null) {
            float distance = calculateDistance(
                    metadata.getLastLatitude(),
                    metadata.getLastLongitude(),
                    event.getLocation().getLatitude(),
                    event.getLocation().getLongitude()
            );

            long timeDiff = event.getTimestamp() - metadata.getLastEventTime().toEpochMilli();

            enrichmentBuilder
                    .distanceFromPrevious(distance)
                    .timeSincePrevious(timeDiff);

            // Calculate speed if time difference is reasonable
            if (timeDiff > 0 && timeDiff < 3600000) { // less than 1 hour
                float calculatedSpeed = (distance / (timeDiff / 3600000.0f)); // km/h
                enrichmentBuilder.calculatedSpeed(calculatedSpeed);
            }
        }



        // Detect anomalies
        List<String> anomalyReasons = detectAnomalies(event, metadata);
        enrichmentBuilder
                .isAnomaly(!anomalyReasons.isEmpty())
                .anomalyReasons(anomalyReasons);


        // Enrich with weather (placeholder - would integrate with weather API)
        String weatherCondition = estimateWeatherFromTelemetry(event.getTelemetry());
        enrichmentBuilder.weatherCondition(weatherCondition);

        // Update metadata cache
        updateDeviceMetadata(deviceId, event);

        log.debug("Enriched event for device {}: isAnomaly={}",
                deviceId, !anomalyReasons.isEmpty());

        return enrichmentBuilder.build();
    }


    private List<String> detectAnomalies(TrackingEventProto.TrackingEvent event,
                                         EventMetadata metadata) {
        List<String> anomalies = new ArrayList<>();

        if (metadata != null) {
            // Check for impossible speed
            if (metadata.getLastLatitude() != null) {
                float distance = calculateDistance(
                        metadata.getLastLatitude(),
                        metadata.getLastLongitude(),
                        event.getLocation().getLatitude(),
                        event.getLocation().getLongitude()
                );

                long timeDiff = event.getTimestamp() - metadata.getLastEventTime().toEpochMilli();
                if (timeDiff > 0) {
                    float calculatedSpeed = (distance / (timeDiff / 3600000.0f));
                    if (calculatedSpeed > 250.0f) { // > 250 km/h
                        anomalies.add("IMPOSSIBLE_SPEED");
                    }
                }
            }

            // Check for GPS jump
            if (metadata.getLastLatitude() != null) {
                float distance = calculateDistance(
                        metadata.getLastLatitude(),
                        metadata.getLastLongitude(),
                        event.getLocation().getLatitude(),
                        event.getLocation().getLongitude()
                );

                if (distance > 100.0f) { // > 100 km jump
                    long timeDiff = event.getTimestamp() - metadata.getLastEventTime().toEpochMilli();
                    if (timeDiff < 60000) { // in less than 1 minute
                        anomalies.add("GPS_JUMP");
                    }
                }
            }


        }


        // Check for low satellite count
        if (event.getLocation().getSatellites() < 4) {
            anomalies.add("LOW_SATELLITE_COUNT");
        }

        // Check for extreme temperature
        float temp = event.getTelemetry().getTemperature();
        if (temp < -10 || temp > 50) {
            anomalies.add("EXTREME_TEMPERATURE");
        }

        return anomalies;
    }



    private String estimateWeatherFromTelemetry(ProcessedEventProto.TelemetryData telemetry) {
        float temp = telemetry.getTemperature();
        float humidity = telemetry.getHumidity();

        // Simple heuristic - in production, use weather API
        if (humidity > 80 && temp > 10) {
            return "RAINY";
        } else if (temp < 0) {
            return "COLD";
        } else if (temp > 30) {
            return "HOT";
        } else if (humidity < 30) {
            return "DRY";
        } else {
            return "NORMAL";
        }
    }

    private float calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Haversine formula
        final int EARTH_RADIUS_KM = 6371;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return (float) (EARTH_RADIUS_KM * c);
    }

    private void updateDeviceMetadata(String deviceId, TrackingEventProto.TrackingEvent event) {
        EventMetadata metadata = deviceMetadataCache.computeIfAbsent(
                deviceId,
                k -> EventMetadata.builder()
                        .deviceId(deviceId)
                        .eventCount(0)
                        .totalDistance(0.0f)
                        .firstEventTime(Instant.ofEpochMilli(event.getTimestamp()))
                        .build()
        );

        metadata.setLastEventTime(Instant.ofEpochMilli(event.getTimestamp()));
        metadata.setLastLatitude(event.getLocation().getLatitude());
        metadata.setLastLongitude(event.getLocation().getLongitude());
        metadata.setEventCount(metadata.getEventCount() + 1);

        metadata.setLastTemperature(event.getTelemetry().getTemperature());
        metadata.setLastHumidity(event.getTelemetry().getHumidity());
    }

    @Cacheable(value = "locationCache", key = "#latitude + '_' + #longitude")
    public String reverseGeocode(double latitude, double longitude) {
        // Placeholder - in production, integrate with geocoding API
        // (Google Maps, OpenStreetMap Nominatim, etc.)
        log.debug("Reverse geocoding: ({}, {})", latitude, longitude);
        return "Unknown Location";
    }
}

