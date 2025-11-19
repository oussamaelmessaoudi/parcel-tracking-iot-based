package com.tracksecure.alertservice.engine;

import com.fasterxml.jackson.databind.JsonNode;
import com.tracksecure.alertservice.model.Geofence;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.GeometryFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class GeofenceEngine {

    private final GeometryFactory geometryFactory = new GeometryFactory();
    private final Map<String, Boolean> deviceGeofenceState = new HashMap<>();

    public GeofenceViolation checkGeofence(Geofence geofence,
                                           double latitude,
                                           double longitude,
                                           String deviceId) {

        boolean isInside = isPointInsideGeofence(geofence, latitude, longitude);
        String stateKey = deviceId + ":" + geofence.getGeofenceId();
        Boolean previousState = deviceGeofenceState.get(stateKey);

        GeofenceViolation violation = null;

        if (previousState == null) {
            // First time checking this device/geofence combination
            deviceGeofenceState.put(stateKey, isInside);

        } else if (previousState != isInside) {
            // State changed
            if (isInside && geofence.getAlertOnEntry()) {
                violation = GeofenceViolation.builder()
                        .geofenceId(geofence.getGeofenceId())
                        .geofenceName(geofence.getName())
                        .violationType(GeofenceViolation.ViolationType.ENTRY)
                        .latitude(latitude)
                        .longitude(longitude)
                        .build();

                log.info("Geofence entry detected: Device {} entered {}",
                        deviceId, geofence.getName());

            } else if (!isInside && geofence.getAlertOnExit()) {
                violation = GeofenceViolation.builder()
                        .geofenceId(geofence.getGeofenceId())
                        .geofenceName(geofence.getName())
                        .violationType(GeofenceViolation.ViolationType.EXIT)
                        .latitude(latitude)
                        .longitude(longitude)
                        .build();

                log.info("Geofence exit detected: Device {} exited {}",
                        deviceId, geofence.getName());
            }

            deviceGeofenceState.put(stateKey, isInside);
        }

        return violation;
    }

    private boolean evaluateTemperature(JsonNode conditions, RuleContext context) {
        if (context.getTemperature() == null) return false;

        if (conditions.has("min_temperature")) {
            double minTemp = conditions.get("min_temperature").asDouble();
            if (context.getTemperature() < minTemp) return true;
        }

        if (conditions.has("max_temperature")) {
            double maxTemp = conditions.get("max_temperature").asDouble();
            if (context.getTemperature() > maxTemp) return true;
        }

        return false;
    }

    private boolean evaluateHumidity(JsonNode conditions, RuleContext context) {
        if (context.getHumidity() == null) return false;

        if (conditions.has("min_humidity")) {
            double minHumidity = conditions.get("min_humidity").asDouble();
            if (context.getHumidity() < minHumidity) return true;
        }

        if (conditions.has("max_humidity")) {
            double maxHumidity = conditions.get("max_humidity").asDouble();
            if (context.getHumidity() > maxHumidity) return true;
        }

        return false;
    }

    private boolean evaluateOfflineTimeout(JsonNode conditions, RuleContext context) {
        if (context.getLastSeenMinutesAgo() == null) return false;

        int timeoutMinutes = conditions.get("timeout_minutes").asInt();
        return context.getLastSeenMinutesAgo() > timeoutMinutes;
    }

    private boolean evaluateCustomExpression(JsonNode conditions, RuleContext context) {
        // Placeholder for custom expression evaluation
        // In production, use a safe expression evaluator like JEXL or SpEL
        log.debug("Custom expression evaluation not implemented");
        return false;
    }

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class RuleContext {
        private String deviceId;
        private Double latitude;
        private Double longitude;
        private Float temperature;
        private Float humidity;
        private Integer satellites;
        private Long lastSeenMinutesAgo;
    }
}