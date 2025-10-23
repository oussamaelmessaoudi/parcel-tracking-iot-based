package com.tracksecure.sensorspayload.model;


import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Payload {
    private float temperature;
    private float humidity;
    private LocalDateTime timestamp;
}
