package com.tracksecure.alertservice.service;


import com.tracksecure.alertservice.model.Alert;
import com.tracksecure.alertservice.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertService {

    private final AlertRepository alertRepository;
    private final NotificationService notificationService;

    @Transactional
    public Alert createAlert(Alert alert) {
        Alert savedAlert = alertRepository.save(alert);

        log.info("Created alert {} for device {} - Type: {}, Severity: {}",
                savedAlert.getAlertId(),
                savedAlert.getDeviceId(),
                savedAlert.getAlertType(),
                savedAlert.getSeverity());

        // Send notifications asynchronously
        notificationService.sendAlertNotification(savedAlert);

        return savedAlert;
    }

    @Transactional(readOnly = true)
    public Alert getAlert(String alertId) {
        return alertRepository.findByAlertId(alertId)
                .orElseThrow(() -> new RuntimeException("Alert not found: " + alertId));
    }

    @Transactional(readOnly = true)
    public List<Alert> getDeviceAlerts(String deviceId) {
        return alertRepository.findByDeviceIdOrderByTimestampDesc(deviceId);
    }

    @Transactional(readOnly = true)
    public Page<Alert> getDeviceAlertsPaged(String deviceId, int page, int size) {
        return alertRepository.findByDeviceIdOrderByTimestampDesc(
                deviceId, PageRequest.of(page, size));
    }

    @Transactional(readOnly = true)
    public List<Alert> getUnacknowledgedAlerts(String deviceId) {
        return alertRepository.findByDeviceIdAndAcknowledgedFalseOrderByTimestampDesc(deviceId);
    }

    @Transactional(readOnly = true)
    public List<Alert> getCriticalAlerts() {
        return alertRepository.findUnacknowledgedCriticalAlerts();
    }

    @Transactional
    public Alert acknowledgeAlert(String alertId, String acknowledgedBy) {
        Alert alert = getAlert(alertId);

        if (!alert.getAcknowledged()) {
            alert.setAcknowledged(true);
            alert.setAcknowledgedAt(Instant.now());
            alert.setAcknowledgedBy(acknowledgedBy);

            Alert updated = alertRepository.save(alert);

            log.info("Alert {} acknowledged by {}", alertId, acknowledgedBy);

            return updated;
        }

        return alert;
    }

    @Transactional(readOnly = true)
    public boolean hasRecentAlert(String deviceId, String ruleId, int cooldownMinutes) {
        Instant since = Instant.now().minusSeconds(cooldownMinutes * 60L);
        long count = alertRepository.countRecentAlertsByRule(deviceId, ruleId, since);
        return count > 0;
    }

    @Transactional(readOnly = true)
    public List<Alert> getAlertsByTimeRange(Instant startTime, Instant endTime) {
        return alertRepository.findAlertsByTimeRange(startTime, endTime);
    }
}