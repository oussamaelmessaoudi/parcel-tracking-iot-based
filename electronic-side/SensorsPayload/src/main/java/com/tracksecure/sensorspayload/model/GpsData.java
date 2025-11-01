package com.tracksecure.sensorspayload.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class GpsData {
    private double  latitude;
    private double longitude;
    private int satellites;
    private LocalDateTime timestamp;
}
