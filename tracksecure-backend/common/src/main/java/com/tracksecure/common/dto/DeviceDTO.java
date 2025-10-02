package com.tracksecure.common.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeviceDTO {
    @NotBlank(message = "Device ID is required")
    private String deviceId;

    @NotBlank(message = "Serial number is required")
    private String serialNumber;

    @NotBlank(message = "Model is required")
    private String model;

    @NotBlank(message = "Firmware version is required")
    @Size(max = 50)
    private String firmwareVersion;

    @NotBlank(message = "Status must be specified")
    private String status;

    @PastOrPresent(message = "Last communication time cannot be in the future")
    private LocalDateTime lastCommunicationTime;

    @Size(max = 50)
    private String location;

    @Size(max = 50)
    private String assignedShipementId;
}
