package com.tracksecure.alertservice.engine;

import com.tracksecure.alertservice.model.Alert;
import com.tracksecure.alertservice.model.AlertRule;
import com.tracksecure.alertservice.model.Geofence;
import com.tracksecure.alertservice.repository.AlertRuleRepository;
import com.tracksecure.alertservice.repository.GeofenceRepository;
import com.tracksecure.alertservice.service.AlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlertEngine {

    private final AlertRuleRepository alertRuleRepository;
    private final GeofenceRepository geofenceRepository;
    private final RuleEngine ruleEngine;
    private final GeofenceEngine geofenceEngine;
    private final AlertService alertService;

    public void processLocationUpdate(String deviceId,
                                      double latitude,
                                      double longitude,
                                      Float temperature,
                                      Float humidity) {

        log.debug("Processing location update for device: {}", deviceId);

        // Check geofences
        checkGeofences(deviceId, latitude, longitude);

        // Check alert rules
        checkAlertRules(deviceId, latitude, longitude,
                temperature, humidity);
    }

    private void checkGeofences(String deviceId, double latitude, double longitude) {
        List<Geofence> geofences = geofenceRepository.findApplicableGeofences(deviceId);

        for (Geofence geofence : geofences) {
            GeofenceEngine.GeofenceViolation violation =
                    geofenceEngine.checkGeofence(geofence, latitude, longitude, deviceId);

            if (violation != null) {
                createGeofenceAlert(deviceId, geofence, violation);
            }
        }
    }

    private void checkAlertRules(String deviceId,
                                 double latitude,
                                 double longitude,
                                 Float temperature,
                                 Float humidity) {

        List<AlertRule> rules = alertRuleRepository.findByDeviceIdAndEnabledTrue(deviceId);

        RuleEngine.RuleContext context = RuleEngine.RuleContext.builder()
                .deviceId(deviceId)
                .latitude(latitude)
                .longitude(longitude)
                .temperature(temperature)
                .humidity(humidity)
                .build();

        for (AlertRule rule : rules) {
            // Check cooldown period
            if (isInCooldown(deviceId, rule)) {
                log.debug("Rule {} is in cooldown period for device {}",
                        rule.getRuleId(), deviceId);
                continue;
            }

            // Evaluate rule
            boolean triggered = ruleEngine.evaluateRule(rule, context);

            if (triggered) {
                createRuleAlert(deviceId, rule, context);
            }
        }
    }

    private boolean isInCooldown(String deviceId, AlertRule rule) {
        if (rule.getCooldownMinutes() == null || rule.getCooldownMinutes() == 0) {
            return false;
        }

        return alertService.hasRecentAlert(deviceId, rule.getRuleId(),
                rule.getCooldownMinutes());
    }

    private void createGeofenceAlert(String deviceId,
                                     Geofence geofence,
                                     GeofenceEngine.GeofenceViolation violation) {

        Alert.AlertType alertType = violation.getViolationType() ==
                GeofenceEngine.GeofenceViolation.ViolationType.ENTRY ?
                Alert.AlertType.GEOFENCE_ENTRY : Alert.AlertType.GEOFENCE_EXIT;

        String title = String.format("Geofence %s: %s",
                violation.getViolationType().name(),
                violation.getGeofenceName());

        String message = String.format("Device %s %s geofence '%s' at location (%.6f, %.6f)",
                deviceId,
                violation.getViolationType() ==
                        GeofenceEngine.GeofenceViolation.ViolationType.ENTRY ?
                        "entered" : "exited",
                violation.getGeofenceName(),
                violation.getLatitude(),
                violation.getLongitude());

        Alert alert = Alert.builder()
                .alertId(UUID.randomUUID().toString())
                .deviceId(deviceId)
                .alertType(alertType)
                .severity(Alert.AlertSeverity.WARNING)
                .title(title)
                .message(message)
                .latitude(violation.getLatitude())
                .longitude(violation.getLongitude())
                .timestamp(java.time.Instant.now())
                .build();

        alertService.createAlert(alert);
    }

    private void createRuleAlert(String deviceId,
                                 AlertRule rule,
                                 RuleEngine.RuleContext context) {

        Alert.AlertType alertType = mapRuleTypeToAlertType(rule.getRuleType());

        String message = buildAlertMessage(rule, context);

        Map<String, String> metadata = new HashMap<>();
        metadata.put("rule_id", rule.getRuleId());
        metadata.put("rule_name", rule.getName());

        if (context.getTemperature() != null) {
            metadata.put("temperature", context.getTemperature().toString());
        }
        if (context.getHumidity() != null) {
            metadata.put("humidity", context.getHumidity().toString());
        }

        Alert alert = Alert.builder()
                .alertId(UUID.randomUUID().toString())
                .deviceId(deviceId)
                .ruleId(rule.getRuleId())
                .alertType(alertType)
                .severity(rule.getSeverity())
                .title(rule.getName())
                .message(message)
                .latitude(context.getLatitude())
                .longitude(context.getLongitude())
                .timestamp(java.time.Instant.now())
                .metadata(new com.fasterxml.jackson.databind.ObjectMapper()
                        .valueToTree(metadata).toString())
                .build();

        alertService.createAlert(alert);
    }

    private Alert.AlertType mapRuleTypeToAlertType(AlertRule.RuleType ruleType) {
        return switch (ruleType) {
            case TEMPERATURE -> Alert.AlertType.TEMPERATURE_THRESHOLD;
            case HUMIDITY -> Alert.AlertType.HUMIDITY_THRESHOLD;
            case OFFLINE_TIMEOUT -> Alert.AlertType.DEVICE_OFFLINE;
            default -> Alert.AlertType.CUSTOM_RULE;
        };
    }

    private String buildAlertMessage(AlertRule rule, RuleEngine.RuleContext context) {
        return switch (rule.getRuleType()) {
            case SPEED_LIMIT -> String.format(
                    "Device %s exceeded speed limit: %.1f km/h",
                    context.getDeviceId(), context.getSpeed()
            );
            case BATTERY_LEVEL -> String.format(
                    "Device %s battery level is low: %.1f%%",
                    context.getDeviceId(), context.getBatteryLevel()
            );
            case TEMPERATURE -> String.format(
                    "Device %s temperature threshold exceeded: %.1f°C",
                    context.getDeviceId(), context.getTemperature()
            );
            case HUMIDITY -> String.format(
                    "Device %s humidity threshold exceeded: %.1f%%",
                    context.getDeviceId(), context.getHumidity()
            );
            case OFFLINE_TIMEOUT -> String.format(
                    "Device %s has been offline for too long",
                    context.getDeviceId()
            );
            default -> String.format(
                    "Alert triggered for device %s: %s",
                    context.getDeviceId(), rule.getName()
            );
        };
    }
}
