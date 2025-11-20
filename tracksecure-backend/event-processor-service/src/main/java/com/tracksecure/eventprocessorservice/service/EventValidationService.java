package com.tracksecure.eventprocessorservice.service;

import com.tracksecure.eventprocessorservice.model.ProcessedEvent;
import com.tracksecure.proto.TrackingEventProto;
import com.tracksecure.proto.ProcessedEventProto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class EventValidationService {

    private static final double MIN_LATITUDE = -90.0;
    private static final double MAX_LATITUDE = 90.0;
    private static final double MIN_LONGITUDE = -180.0;
    private static final double MAX_LONGITUDE = 180.0;
    private static final float MIN_ACCURACY = 0.0f;
    private static final float MAX_ACCURACY = 1000.0f;
    private static final int MIN_SATELLITES = 0;
    private static final int MAX_SATELLITES = 50;

    public ProcessedEvent.ValidationInfo validate(TrackingEventProto.TrackingEvent event) {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        // Validate device ID
        if (event.getDeviceId() == null || event.getDeviceId().isEmpty()) {
            errors.add("Device ID is missing");
        }

        // Validate timestamp
        if (event.getTimestamp() <= 0) {
            errors.add("Invalid timestamp");
        }

        long currentTime = System.currentTimeMillis();
        long eventAge = currentTime - event.getTimestamp();

        if (eventAge < 0) {
            warnings.add("Event timestamp is in the future");
        } else if (eventAge > 3600000) { // 1 hour
            warnings.add("Event is older than 1 hour");
        }

        ProcessedEventProto.LocationData location = event.getLocation();
        if (location != null) {
            validateLocation(location, errors, warnings);
        } else {
            errors.add("Location data is missing");
        }

        // Validate telemetry
        ProcessedEventProto.TelemetryData telemetry = event.getTelemetry();
        if (telemetry != null) {
            validateTelemetry(telemetry, warnings);
        }

        // Calculate confidence score
        float confidenceScore = calculateConfidenceScore(event, errors, warnings);

        boolean isValid = errors.isEmpty();

        log.debug("Validation result for device {}: valid={}, errors={}, warnings={}, confidence={}",
                event.getDeviceId(), isValid, errors.size(), warnings.size(), confidenceScore);

        return ProcessedEvent.ValidationInfo.builder()
                .isValid(isValid)
                .validationErrors(errors)
                .warnings(warnings)
                .confidenceScore(confidenceScore)
                .build();
    }

    private void validateLocation(ProcessedEventProto.LocationData location,
                                  List<String> errors,
                                  List<String> warnings) {
        // Latitude validation
        if (location.getLatitude() < MIN_LATITUDE || location.getLatitude() > MAX_LATITUDE) {
            errors.add(String.format("Invalid latitude: %.6f", location.getLatitude()));
        }

        // Longitude validation
        if (location.getLongitude() < MIN_LONGITUDE || location.getLongitude() > MAX_LONGITUDE) {
            errors.add(String.format("Invalid longitude: %.6f", location.getLongitude()));
        }

        // Check for null island (0,0)
        if (location.getLatitude() == 0.0 && location.getLongitude() == 0.0) {
            warnings.add("Location is at null island (0,0)");
        }

        // Satellites validation
        if (location.getSatellites() < MIN_SATELLITES || location.getSatellites() > MAX_SATELLITES) {
            warnings.add(String.format("Unusual satellite count: %d", location.getSatellites()));
        } else if (location.getSatellites() < 4) {
            warnings.add("Low satellite count may indicate poor GPS signal");
        }
    }

    private void validateTelemetry(ProcessedEventProto.TelemetryData telemetry,
                                   List<String> warnings) {

        // Temperature validation (reasonable range for devices: -20 to 60°C)
        if (telemetry.getTemperature() < -20 || telemetry.getTemperature() > 60) {
            warnings.add(String.format("Extreme temperature: %.2f°C", telemetry.getTemperature()));
        }

        // Humidity validation (0-100%)
        if (telemetry.getHumidity() < 0 || telemetry.getHumidity() > 100) {
            warnings.add(String.format("Invalid humidity: %.2f%%", telemetry.getHumidity()));
        }
    }

    private float calculateConfidenceScore(TrackingEventProto.TrackingEvent event,
                                           List<String> errors,
                                           List<String> warnings) {
        float score = 100.0f;

        // Deduct points for errors (critical)
        score -= errors.size() * 25.0f;

        // Deduct points for warnings (less critical)
        score -= warnings.size() * 5.0f;

        // Bonus for good satellite count
        if (event.hasLocation() && event.getLocation().getSatellites() >= 8) {
            score += 5.0f;
        }

        // Clamp between 0 and 100
        return Math.max(0.0f, Math.min(100.0f, score));
    }
}