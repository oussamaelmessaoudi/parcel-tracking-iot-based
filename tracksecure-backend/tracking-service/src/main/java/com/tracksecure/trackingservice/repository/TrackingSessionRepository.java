package com.tracksecure.trackingservice.repository;

import com.tracksecure.trackingservice.model.TrackingSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface TrackingSessionRepository extends JpaRepository<TrackingSession, Long> {
    Optional<TrackingSession> findBySessionId(String sessionId);

    List<TrackingSession> findByDeviceIdOrderByStartTimeDesc(String deviceId);

    Optional<TrackingSession> findFirstByDeviceIdAndStatusOrderByStartTimeDesc(
            String deviceId, TrackingSession.SessionStatus status);

    @Query("SELECT s FROM TrackingSession s WHERE s.deviceId = :deviceId " +
            "AND s.status = 'ACTIVE'")
    Optional<TrackingSession> findActiveSession(@Param("deviceId") String deviceId);

    List<TrackingSession> findByStatusAndStartTimeBefore(
            TrackingSession.SessionStatus status, Instant before);
}
