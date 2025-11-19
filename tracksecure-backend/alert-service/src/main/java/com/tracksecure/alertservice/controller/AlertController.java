package com.tracksecure.alertservice.controller;

import com.tracksecure.alertservice.dto.AlertDTO;
import com.tracksecure.alertservice.model.Alert;
import com.tracksecure.alertservice.service.AlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Coordinate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static com.tracksecure.alertservice.model.Geofence.GeofenceType.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    @GetMapping("/{alertId}")
    public ResponseEntity<AlertDTO> getAlert(@PathVariable String alertId) {
        Alert alert = alertService.getAlert(alertId);
        return ResponseEntity.ok(mapToDTO(alert));
    }

    @GetMapping("/device/{deviceId}")
    public ResponseEntity<List<AlertDTO>> getDeviceAlerts(@PathVariable String deviceId) {
        List<Alert> alerts = alertService.getDeviceAlerts(deviceId);
        List<AlertDTO> dtos = alerts.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/device/{deviceId}/unacknowledged")
    public ResponseEntity<List<AlertDTO>> getUnacknowledgedAlerts(@PathVariable String deviceId) {
        List<Alert> alerts = alertService.getUnacknowledgedAlerts(deviceId);
        List<AlertDTO> dtos = alerts.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/critical")
    public ResponseEntity<List<AlertDTO>> getCriticalAlerts() {
        List<Alert> alerts = alertService.getCriticalAlerts();
        List<AlertDTO> dtos = alerts.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PostMapping("/{alertId}/acknowledge")
    public ResponseEntity<AlertDTO> acknowledgeAlert(
            @PathVariable String alertId,
            @RequestParam String acknowledgedBy) {
        Alert alert = alertService.acknowledgeAlert(alertId, acknowledgedBy);
        return ResponseEntity.ok(mapToDTO(alert));
    }

    @GetMapping("/time-range")
    public ResponseEntity<List<AlertDTO>> getAlertsByTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endTime) {
        List<Alert> alerts = alertService.getAlertsByTimeRange(startTime, endTime);
        List<AlertDTO> dtos = alerts.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    private AlertDTO mapToDTO(Alert alert) {
        return AlertDTO.builder()
                .alertId(alert.getAlertId())
                .deviceId(alert.getDeviceId())
                .ruleId(alert.getRuleId())
                .timestamp(alert.getTimestamp())
                .alertType(alert.getAlertType().name())
                .severity(alert.getSeverity().name())
                .title(alert.getTitle())
                .message(alert.getMessage())
                .latitude(alert.getLatitude())
                .longitude(alert.getLongitude())
                .acknowledged(alert.getAcknowledged())
                .acknowledgedAt(alert.isPointInsideGeofence(Geofence geofence, double latitude,
        double longitude) {
            Point point = geometryFactory.createPoint(new Coordinate(longitude, latitude));

            return switch (geofence.getType()) {
                case CIRCLE -> isPointInCircle(
                        point,
                        geofence.getCenterLatitude(),
                        geofence.getCenterLongitude(),
                        geofence.getRadiusMeters()
                );
                case POLYGON -> isPointInPolygon(point, geofence.getPolygonCoordinates());
                case RECTANGLE -> isPointInRectangle(point, geofence.getPolygonCoordinates());
            };
        }

        private boolean isPointInCircle(Point point,
        double centerLat,
        double centerLon,
        double radiusMeters) {
            double distance = calculateDistance(
                    point.getY(), point.getX(),
                    centerLat, centerLon
            );
            return distance <= radiusMeters;
        }

        private boolean isPointInPolygon(Point point, String polygonCoordinates) {
            // Parse polygon coordinates and check if point is inside
            // Format: "lat1,lon1;lat2,lon2;lat3,lon3"
            if (polygonCoordinates == null || polygonCoordinates.isEmpty()) {
                return false;
            }

            try {
                String[] coords = polygonCoordinates.split(";");
                Coordinate[] coordinates = new Coordinate[coords.length + 1];

                for (int i = 0; i < coords.length; i++) {
                    String[] latLon = coords[i].split(",");
                    coordinates[i] = new Coordinate(
                            Double.parseDouble(latLon[1]),
                            Double.parseDouble(latLon[0])
                    );
                }

                // Close the polygon
                coordinates[coords.length] = coordinates[0];

                Polygon polygon = geometryFactory.createPolygon(coordinates);
                return polygon.contains(point);

            } catch (Exception e) {
                log.error("Error parsing polygon coordinates: {}", e.getMessage());
                return false;
            }
        }

        private boolean isPointInRectangle(Point point, String rectangleCoordinates) {
            // Similar to polygon but ensures rectangle shape
            return isPointInPolygon(point, rectangleCoordinates);
        }

        private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
            // Haversine formula
            final int EARTH_RADIUS_METERS = 6371000;

            double dLat = Math.toRadians(lat2 - lat1);
            double dLon = Math.toRadians(lon2 - lon1);

            double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                    Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                            Math.sin(dLon / 2) * Math.sin(dLon / 2);

            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

            return EARTH_RADIUS_METERS * c;
        }

        @lombok.Data
        @lombok.Builder
        @lombok.NoArgsConstructor
        @lombok.AllArgsConstructor
        public static class GeofenceViolation {
            private String geofenceId;
            private String geofenceName;
            private ViolationType violationType;
            private double latitude;
            private double longitude;

            public enum ViolationType {
                ENTRY,
                EXIT
            }
        }
    }