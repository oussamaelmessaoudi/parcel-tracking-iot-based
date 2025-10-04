package com.tracksecure.iotgatewayservice.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RedisHash("DeviceCredential")
public class DeviceCredential implements Serializable {
    @Id
    private String deviceId;

    @Indexed
    private String serialNumber;
    private String secretKey;
    private boolean active;
    private LocalDateTime lastValidatedAt;
    private String firmwareVersion;
    private String assignedShipmentId;
}
