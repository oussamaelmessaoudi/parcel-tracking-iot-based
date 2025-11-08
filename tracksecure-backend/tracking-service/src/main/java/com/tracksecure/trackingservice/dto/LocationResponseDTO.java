package com.tracksecure.trackingservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationResponseDTO {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("device_id")
    private String deviceId;

    @JsonProperty("event_id")
    private String eventId;

    @JsonProperty("timestamp")
    private Instant timestamp;

    @JsonProperty("latitude")
    private Double latitude;

    @JsonProperty("longitude")
    private Double longitude;

    @JsonProperty("satellites")
    private Integer satellites;

    @JsonProperty("temperature")
    private Float temperature;

    @JsonProperty("humidity")
    private Float humidity;

    @JsonProperty("session_id")
    private String sessionId;

    @JsonProperty("created_at")
    private Instant createdAt;
}
