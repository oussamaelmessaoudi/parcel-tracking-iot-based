package com.tracksecure.mqttrestapp.dtos;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SensorDataEvent {
    private Double temperature;
    private Double humidity;
    private Double latitude;
    private Double longitude;
    private Integer satellites;


}
