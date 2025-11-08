package com.tracksecure.trackingservice.service;

import com.tracksecure.trackingservice.dto.LocationRequestDTO;
import com.tracksecure.trackingservice.dto.LocationResponseDTO;
import com.tracksecure.trackingservice.dto.TrackingHistoryDTO;
import com.tracksecure.trackingservice.mapper.LocationMapper;
import com.tracksecure.trackingservice.model.Location;
import com.tracksecure.trackingservice.repository.LocationRepository;
import com.tracksecure.trackingservice.validators.DeviceNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationService {
    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;

    @Transactional
    public LocationResponseDTO saveLocation(LocationRequestDTO requestDTO) {
        Location location = locationMapper.toEntity(requestDTO);
        location.setEventId(java.util.UUID.randomUUID().toString());

        Location savedLocation = locationRepository.save(location);
        log.info("Saved location for device: {} at ({}, {})",
                savedLocation.getDeviceId(),
                savedLocation.getLatitude(),
                savedLocation.getLongitude());

        return locationMapper.toDto(savedLocation);
    }

    @Transactional(readOnly = true)
    public LocationResponseDTO getCurrentLocation(String deviceId) {
        Location location = locationRepository.findFirstByDeviceIdOrderByTimestampDesc(deviceId)
                .orElseThrow(() -> new DeviceNotFoundException("No location found for device: " + deviceId));

        return locationMapper.toDto(location);
    }

    @Transactional(readOnly = true)
    public List<LocationResponseDTO> getLocationHistory(String deviceId, int limit) {
        PageRequest pageRequest = PageRequest.of(0, limit);
        Page<Location> locations = locationRepository.findByDeviceIdOrderByTimestampDesc(deviceId, pageRequest);

        return locations.getContent().stream()
                .map(locationMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TrackingHistoryDTO getTrackingHistory(String deviceId, Instant startTime, Instant endTime) {
        List<Location> locations = locationRepository.findLocationHistory(deviceId, startTime, endTime);

        if (locations.isEmpty()) {
            throw new DeviceNotFoundException("No tracking data found for device: " + deviceId);
        }

        List<LocationResponseDTO> locationDTOs = locations.stream()
                .map(locationMapper::toDto)
                .collect(Collectors.toList());

        TrackingHistoryDTO.TrackingStatistics stats = calculateStatistics(locations);
        Double totalDistance = calculateTotalDistance(locations);

        return TrackingHistoryDTO.builder()
                .deviceId(deviceId)
                .startTime(startTime)
                .endTime(endTime)
                .totalPoints(locations.size())
                .totalDistance(totalDistance)
                .duration(endTime.getEpochSecond() - startTime.getEpochSecond())
                .locations(locationDTOs)
                .statistics(stats)
                .build();
    }

    private TrackingHistoryDTO.TrackingStatistics calculateStatistics(List<Location> locations) {
        if (locations.isEmpty()) {
            return new TrackingHistoryDTO.TrackingStatistics();
        }


        double avgTemperature = locations.stream()
                .filter(l -> l.getTemperature() != null)
                .mapToDouble(Location::getTemperature)
                .average()
                .orElse(0.0);

        double avgHumidity = locations.stream()
                .filter(l -> l.getHumidity() != null)
                .mapToDouble(Location::getHumidity)
                .average()
                .orElse(0.0);


        double avgSatellites = locations.stream()
                .filter(l -> l.getSatellites() != -1)
                .mapToDouble(Location::getSatellites)
                .average()
                .orElse(0.0);

        return TrackingHistoryDTO.TrackingStatistics.builder()
                .avgTemperature(avgTemperature)
                .avgHumidity(avgHumidity)
                .avgSatellites(avgSatellites)
                .build();
    }

    private Double calculateTotalDistance(List<Location> locations) {
        if (locations.size() < 2) {
            return 0.0;
        }

        double totalDistance = 0.0;
        for (int i = 0; i < locations.size() - 1; i++) {
            Location loc1 = locations.get(i);
            Location loc2 = locations.get(i + 1);
            totalDistance += calculateDistance(
                    loc1.getLatitude(), loc1.getLongitude(),
                    loc2.getLatitude(), loc2.getLongitude()
            );
        }

        return totalDistance;
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Haversine formula
        final int EARTH_RADIUS_KM = 6371;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    @Transactional(readOnly = true)
    public List<String> getActiveDevices(int minutesAgo) {
        Instant since = Instant.now().minusSeconds(minutesAgo * 60L);
        return locationRepository.findActiveDevices(since);
    }
}
