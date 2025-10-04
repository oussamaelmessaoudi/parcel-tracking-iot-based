package com.tracksecure.iotgatewayservice.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@EqualsAndHashCode(of = "eventId")
public class EnrichedEvent {
    @NotBlank(message = "Event ID is required")
    private String eventId;
    @NotBlank(message = "Device ID is required")
    private String deviceId;
    @NotBlank(message = "Shipment ID is required")
    private String shipmentId;

    @NotBlank(message = "Event type is required")
    private String eventType;

    @NotNull(message = "Event timestamp is required")
    @PastOrPresent(message = "Event timestamp mustn't be in the future")
    private LocalDateTime eventTimestamp;

    @NotNull(message = "Received timestamp is required")
    @PastOrPresent(message = "Received timestamp mustn't be in the future")
    private LocalDateTime receivedTimestamp;

    private Double longitude;
    private Double latitude;
    private Double temperature;
    private Double humidity;

    private String firmwareVersion;
    private String transmissionMethod;

    private Map<String, Object> metadata;

    //Enriching the telemetry message with metadata or packaging publishing into Kafka for other microservices consumation
}
