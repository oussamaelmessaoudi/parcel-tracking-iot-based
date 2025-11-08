package com.tracksecure.iotgatewayservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TelemetryData {
    @Max(100)
    @Min(-50)
    @JsonProperty("temperature")
    private Double temperature;

    @Max(100)
    @Min(0)
    @JsonProperty("humidity")
    private Double humidity;
}
