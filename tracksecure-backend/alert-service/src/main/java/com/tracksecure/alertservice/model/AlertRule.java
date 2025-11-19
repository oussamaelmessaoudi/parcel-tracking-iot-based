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
@Table(name = "alert_rules", indexes = {
        @Index(name = "idx_rule_device", columnList = "device_id"),
        @Index(name = "idx_rule_enabled", columnList = "enabled"),
        @Index(name = "idx_rule_type", columnList = "rule_type")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String ruleId;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private String deviceId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RuleType ruleType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Alert.AlertSeverity severity;

    @Column(columnDefinition = "TEXT")
    private String conditions;

    @Builder.Default
    private Boolean enabled = true;

    private Integer cooldownMinutes;

    private String notificationChannels;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    public enum RuleType {
        GEOFENCE,
        TEMPERATURE,
        HUMIDITY,
        OFFLINE_TIMEOUT,
        CUSTOM_EXPRESSION
    }
}

