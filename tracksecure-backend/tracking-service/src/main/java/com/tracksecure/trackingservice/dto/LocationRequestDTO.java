package com.tracksecure.trackingservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tracksecure.trackingservice.dto.validators.LocationValidationGroup;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationRequestDTO {
    @NotBlank(message = "Device ID is required", groups = LocationValidationGroup.class)
    @JsonProperty("device_id")
    private String deviceId;

    @NotNull(message = "Timestamp is required", groups = LocationValidationGroup.class)
    @JsonProperty("timestamp")
    private Long timestamp;

    @NotNull(message = "Latitude is required", groups = LocationValidationGroup.class)
    @Min(value = -90, message = "Latitude must be between -90 and 90")
    @Max(value = 90, message = "Latitude must be between -90 and 90")
    @JsonProperty("latitude")
    private Double latitude;

    @NotNull(message = "Longitude is required", groups = LocationValidationGroup.class)
    @Min(value = -180, message = "Longitude must be between -180 and 180")
    @Max(value = 180, message = "Longitude must be between -180 and 180")
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
}
