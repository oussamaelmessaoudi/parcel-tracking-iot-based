package com.tracksecure.common.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "eventId")
public class TrackingEventDTO {
    @NotBlank
    private String eventId;
    @NotBlank
    private String shipmentId;
    @NotBlank
    private String deviceId;
    @NotBlank
    private String eventType; //LOCATION_UPDATE, TEMP_ALERT
    @PastOrPresent
    private LocalDateTime eventTimestamp;

    private Double latitude;
    private Double longitude;
    private Double temperature;
    private Double humidity;

    @Size(max = 255)
    private String description;

    @NotBlank
    @Size(max = 100)
    private String idempotencyKey;
}
