package com.tracksecure.sensorspayload.model;


import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class DhtData {
    private double temperature;
    private double humidity;
    private LocalDateTime timestamp;

}
