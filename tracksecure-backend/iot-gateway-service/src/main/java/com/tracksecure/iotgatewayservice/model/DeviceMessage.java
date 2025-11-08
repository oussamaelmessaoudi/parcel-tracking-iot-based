package com.tracksecure.iotgatewayservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeviceMessage {
    @NotNull
    @JsonProperty("device_id")
    private String deviceId;

    @NotNull
    @JsonProperty("timestamp")
    private Long timestamp;

    @NotNull
    @JsonProperty("location")
    private LocationData location;

    @NotNull
    @JsonProperty("telemetry")
    private TelemetryData telemetry;

    @JsonProperty("event_type")
    private String eventType;

    @JsonProperty("additional_data")
    private Map<String,String> additionalData;

    private String topic;

}
