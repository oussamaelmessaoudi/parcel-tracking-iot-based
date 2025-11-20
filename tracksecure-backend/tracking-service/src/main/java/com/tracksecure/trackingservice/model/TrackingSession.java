package com.tracksecure.trackingservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.web.bind.support.SessionStatus;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tracking_sessions", indexes = {
        @Index(name = "idx_session_device", columnList = "device_id"),
        @Index(name = "idx_session_status", columnList = "status"),
        @Index(name = "idx_session_start", columnList = "start_time")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackingSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,unique = true)
    private String sessionId;

    @Column(nullable = false)
    private String deviceId;

    @Column(nullable = false)
    private Instant startTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionStatus status;

    private Double totalDistance;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Location> locations = new ArrayList<>();

    public enum SessionStatus {
        ACTIVE,
        PAUSED,
        COMPLETED,
        TERMINATED
    }
}
