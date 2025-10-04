package com.tracksecure.iotgatewayservice.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@EqualsAndHashCode(of = "messageId")
public class DeviceMessage {
    @NotBlank(message = "Message ID is required")
    @Size(max = 50)
    private String messageId;

    @NotBlank(message = "Device ID is required")
    @Size(max = 50)
    private String deviceId;

    @NotBlank(message = "Encrypted payload is required")
    private String encryptedPayload;

    @NotBlank(message = "Signature is required")
    private String signature;

    @NotBlank(message = "Received Timestamp mustn't be blank")
    private Long receivedTimestamp;

    @Size(max=100)
    private String firmwareVersion;

    @Size(max=100)
    private String transmissionMethod;

    //This expected to be a model/ entity of the raw MQTT payload coming from the device*
    //Usually the payload is still encrypted and processed yet
}
