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
@Table(name = "geofences", indexes = {
        @Index(name = "idx_geofence_device", columnList = "device_id"),
        @Index(name = "idx_geofence_enabled", columnList = "enabled")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Geofence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String geofenceId;

    @Column(nullable = false)
    private String name;

    private String description;

    private String deviceId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GeofenceType type;

    private Double centerLatitude;
    private Double centerLongitude;
    private Double radiusMeters;

    @Column(columnDefinition = "TEXT")
    private String polygonCoordinates;

    @Builder.Default
    private Boolean enabled = true;

    @Builder.Default
    private Boolean alertOnEntry = true;

    @Builder.Default
    private Boolean alertOnExit = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    public enum GeofenceType {
        CIRCLE,
        POLYGON,
        RECTANGLE
    }
}