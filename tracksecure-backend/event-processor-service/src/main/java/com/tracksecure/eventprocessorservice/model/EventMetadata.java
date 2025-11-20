package com.tracksecure.eventprocessorservice.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventMetadata {
    private String deviceId;
    private Instant lastEventTime;
    private Double lastLatitude;
    private Double lastLongitude;
    private Integer eventCount;
    private Instant firstEventTime;

    // Device state
    private Boolean isMoving;
    private Float totalDistance;

    // Health metrics
    private Float lastTemperature;
    private Float lastHumidity;
}