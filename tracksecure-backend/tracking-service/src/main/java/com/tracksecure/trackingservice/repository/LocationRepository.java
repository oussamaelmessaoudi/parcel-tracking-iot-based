package com.tracksecure.trackingservice.repository;

import com.tracksecure.trackingservice.model.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {
    List<Location> findByDeviceIdOrderByTimestampDesc(String deviceId);

    Page<Location> findByDeviceIdOrderByTimestampDesc(String deviceId,Pageable pageable);

    Optional<Location> findFirstByDeviceIdOrderByTimestampDesc(String deviceId);

    List<Location> findByDeviceIdAndTimestampBetweenOrderByTimestampAsc(String deviceId, Instant startTime, Instant endTime);

    @Query("SELECT l FROM Location l WHERE l.deviceId = :deviceId " +
            "AND l.timestamp >= :startTime AND l.timestamp <= :endTime " +
            "ORDER BY l.timestamp ASC")
    List<Location> findLocationHistory(
            @Param("deviceId") String deviceId,
            @Param("startTime") Instant startTime,
            @Param("endTime") Instant endTime);

    @Query("SELECT COUNT(l) FROM Location l WHERE l.deviceId = :deviceId " +
            "AND l.timestamp >= :since")
    long countByDeviceIdSince(@Param("deviceId") String deviceId,
                              @Param("since") Instant since);

    @Query("SELECT DISTINCT l.deviceId FROM Location l WHERE l.timestamp >= :since")
    List<String> findActiveDevices(@Param("since") Instant since);

    void deleteByDeviceIdAndTimestampBefore(String deviceId, Instant before);

}
