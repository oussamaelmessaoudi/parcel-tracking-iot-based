package com.tracksecure.common.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = "alertId")
public class AlertDTO {
    @NotBlank(message = "Alert id is required")
    private String alertId;
    @NotBlank(message = "Shipment id is required")
    private String shipmentId;

    @NotBlank(message = "Device id is required")
    private String deviceId;

    @NotBlank(message = "Alert type must be specified")
    private String alertType;

    @NotBlank(message = "Severity is required")
    private String severity;

    @PastOrPresent(message = "Alert timestamp mustn't be in the future")
    private LocalDateTime alertTimestamp;

    @Size(max = 500)
    private String message;

    private boolean ack;

    @PastOrPresent
    private LocalDateTime ackTimestamp;

    @Size(max = 100)
    private String ackedBy;
}
