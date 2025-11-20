package com.tracksecure.eventprocessorservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessedEvent {

    private String eventId;
    private String deviceId;
    private Instant timestamp;
    private Instant processingTimestamp;

    private LocationInfo location;
    private TelemetryInfo telemetry;
    private EnrichmentInfo enrichment;
    private ValidationInfo validation;

    private String eventType;
    private EventStatus status;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocationInfo {
        private Double latitude;
        private Double longitude;
        private Integer satellites;

        // Enriched
        private String country;
        private String city;
        private String address;
        private String timezone;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TelemetryInfo {
        private Float temperature;
        private Float humidity;
        private Map<String, String> additionalData;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EnrichmentInfo {
        private Float distanceFromPrevious;
        private Long timeSincePrevious;
        private Boolean isMoving;
        private Boolean isStationary;
        private Float calculatedSpeed;
        private String motionState;

        // Anomalies
        private Boolean isAnomaly;
        private List<String> anomalyReasons;

        // Weather
        private String weatherCondition;

    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidationInfo {
        private Boolean isValid;
        private List<String> validationErrors;
        private List<String> warnings;
        private Float confidenceScore;
    }

    public enum EventStatus {
        UNKNOWN,
        VALID,
        INVALID,
        SUSPICIOUS,
        ENRICHED
    }

}