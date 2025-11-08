package com.tracksecure.iotgatewayservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationData {
    @NotNull
    @Min(-180)
    @Max(180)
    @JsonProperty("longitude")
    private Double longitude;

    @NotNull
    @Min(-90)
    @Max(90)
    @JsonProperty("latitude")
    private Double latitude;
}
