package com.tracksecure.alertservice.repository;

import com.tracksecure.alertservice.model.Alert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {

    Optional<Alert> findByAlertId(String alertId);

    List<Alert> findByDeviceIdOrderByTimestampDesc(String deviceId);

    Page<Alert> findByDeviceIdOrderByTimestampDesc(String deviceId, Pageable pageable);

    List<Alert> findByDeviceIdAndAcknowledgedFalseOrderByTimestampDesc(String deviceId);

    @Query("SELECT a FROM Alert a WHERE a.acknowledged = false " +
            "AND a.severity IN ('CRITICAL', 'EMERGENCY') " +
            "ORDER BY a.timestamp DESC")
    List<Alert> findUnacknowledgedCriticalAlerts();

    @Query("SELECT a FROM Alert a WHERE a.deviceId = :deviceId " +
            "AND a.timestamp >= :since ORDER BY a.timestamp DESC")
    List<Alert> findRecentAlerts(@Param("deviceId") String deviceId,
                                 @Param("since") Instant since);

    @Query("SELECT COUNT(a) FROM Alert a WHERE a.deviceId = :deviceId " +
            "AND a.ruleId = :ruleId AND a.timestamp >= :since")
    long countRecentAlertsByRule(@Param("deviceId") String deviceId,
                                 @Param("ruleId") String ruleId,
                                 @Param("since") Instant since);

    List<Alert> findBySeverityAndAcknowledgedFalse(Alert.AlertSeverity severity);

    @Query("SELECT a FROM Alert a WHERE a.timestamp >= :startTime " +
            "AND a.timestamp <= :endTime ORDER BY a.timestamp DESC")
    List<Alert> findAlertsByTimeRange(@Param("startTime") Instant startTime,
                                      @Param("endTime") Instant endTime);
}
