package com.tracksecure.trackingservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackingHistoryDTO {
    @JsonProperty("device_id")
    private String deviceId;

    @JsonProperty("start_time")
    private Instant startTime;

    @JsonProperty("end_time")
    private Instant endTime;

    @JsonProperty("total_points")
    private Integer totalPoints;

    @JsonProperty("total_distance")
    private Double totalDistance;

    @JsonProperty("duration")
    private Long duration;

    @JsonProperty("locations")
    private List<LocationResponseDTO> locations;

    @JsonProperty("statistics")
    private TrackingStatistics statistics;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrackingStatistics {

        @JsonProperty("avg_temperature")
        private Double avgTemperature;

        @JsonProperty("avg_humidity")
        private Double avgHumidity;

        @JsonProperty("avg_satellites")
        private Double avgSatellites;
    }
}