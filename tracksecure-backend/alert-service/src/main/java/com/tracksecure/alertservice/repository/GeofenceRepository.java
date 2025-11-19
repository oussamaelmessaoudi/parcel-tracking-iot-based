package com.tracksecure.alertservice.repository;

import com.tracksecure.alertservice.model.Geofence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GeofenceRepository extends JpaRepository<Geofence, Long> {

    Optional<Geofence> findByGeofenceId(String geofenceId);

    List<Geofence> findByEnabledTrue();

    List<Geofence> findByDeviceIdAndEnabledTrue(String deviceId);

    List<Geofence> findByDeviceId(String deviceId);

    @Query("SELECT g FROM Geofence g WHERE g.enabled = true " +
            "AND (g.deviceId = :deviceId OR g.deviceId IS NULL)")
    List<Geofence> findApplicableGeofences(@Param("deviceId") String deviceId);
}