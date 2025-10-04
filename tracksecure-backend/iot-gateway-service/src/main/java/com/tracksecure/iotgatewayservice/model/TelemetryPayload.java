package com.tracksecure.iotgatewayservice.model;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@EqualsAndHashCode(of = "eventId")
public class TelemetryPayload {
    @NotBlank(message = "Event ID is required")
    @Size(max=50)
    private String eventId;

    @NotBlank(message = "Device ID is required")
    @Size(max = 50)
    private String deviceId;

    @NotBlank(message = "Shipment ID is required")
    @Size(max = 50)
    private String shipmentId;

    @NotBlank(message = "Event type is required")
    private String eventType;

    @NotNull(message = "Event timestamp mustn't be null")
    private Long eventTimestamp;

    @DecimalMin(value = "-90.00")
    @DecimalMax(value = "90.00")
    private BigDecimal longitude;

    @DecimalMin(value = "-180.00")
    @DecimalMax(value = "180.00")
    private BigDecimal latitude;

    @DecimalMin(value = "-100.00")
    @DecimalMax(value = "100.00")
    private BigDecimal temperature;

    @DecimalMin(value = "0.00")
    @DecimalMax(value = "100.00")
    private BigDecimal humidity;

    @Size(max=255)
    private String description;

    //After processing the data (decrypted and validated it and pass it from the service layer)
    //
}
