package com.tracksecure.alertservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "alerts", indexes = {
        @Index(name = "idx_device_timestamp", columnList = "device_id,timestamp"),
        @Index(name = "idx_severity", columnList = "severity"),
        @Index(name = "idx_acknowledged", columnList = "acknowledged"),
        @Index(name = "idx_alert_type", columnList = "alert_type")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String alertId;

    @Column(nullable = false)
    private String deviceId;

    private String ruleId;

    @Column(nullable = false)
    private Instant timestamp;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertType alertType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertSeverity severity;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String message;

    private Double latitude;
    private Double longitude;

    @Column(columnDefinition = "TEXT")
    private String metadata;

    @Builder.Default
    private Boolean acknowledged = false;

    private Instant acknowledgedAt;
    private String acknowledgedBy;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    public enum AlertType {
        GEOFENCE_ENTRY,
        GEOFENCE_EXIT,
        SPEED_LIMIT,
        LOW_BATTERY,
        DEVICE_OFFLINE,
        ANOMALY_DETECTED,
        TEMPERATURE_THRESHOLD,
        HUMIDITY_THRESHOLD,
        CUSTOM_RULE
    }

    public enum AlertSeverity {
        INFO,
        WARNING,
        CRITICAL,
        EMERGENCY
    }
}