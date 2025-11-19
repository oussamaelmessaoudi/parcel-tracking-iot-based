package com.tracksecure.alertservice.service;

import com.tracksecure.alertservice.model.AlertRule;
import com.tracksecure.alertservice.repository.AlertRuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertRuleService {

    private final AlertRuleRepository alertRuleRepository;

    @Transactional
    public AlertRule createRule(AlertRule rule) {
        if (rule.getRuleId() == null) {
            rule.setRuleId(UUID.randomUUID().toString());
        }

        AlertRule savedRule = alertRuleRepository.save(rule);

        log.info("Created alert rule {} for device {}: {}",
                savedRule.getRuleId(),
                savedRule.getDeviceId(),
                savedRule.getName());

        return savedRule;
    }

    @Transactional
    public AlertRule updateRule(String ruleId, AlertRule updatedRule) {
        AlertRule existingRule = alertRuleRepository.findByRuleId(ruleId)
                .orElseThrow(() -> new RuntimeException("Rule not found: " + ruleId));

        existingRule.setName(updatedRule.getName());
        existingRule.setDescription(updatedRule.getDescription());
        existingRule.setRuleType(updatedRule.getRuleType());
        existingRule.setSeverity(updatedRule.getSeverity());
        existingRule.setConditions(updatedRule.getConditions());
        existingRule.setEnabled(updatedRule.getEnabled());
        existingRule.setCooldownMinutes(updatedRule.getCooldownMinutes());
        existingRule.setNotificationChannels(updatedRule.getNotificationChannels());

        AlertRule saved = alertRuleRepository.save(existingRule);

        log.info("Updated alert rule {}", ruleId);

        return saved;
    }

    @Transactional
    public void deleteRule(String ruleId) {
        AlertRule rule = alertRuleRepository.findByRuleId(ruleId)
                .orElseThrow(() -> new RuntimeException("Rule not found: " + ruleId));

        alertRuleRepository.delete(rule);

        log.info("Deleted alert rule {}", ruleId);
    }

    @Transactional(readOnly = true)
    public AlertRule getRule(String ruleId) {
        return alertRuleRepository.findByRuleId(ruleId)
                .orElseThrow(() -> new RuntimeException("Rule not found: " + ruleId));
    }

    @Transactional(readOnly = true)
    public List<AlertRule> getDeviceRules(String deviceId) {
        return alertRuleRepository.findByDeviceId(deviceId);
    }

    @Transactional(readOnly = true)
    public List<AlertRule> getEnabledRules() {
        return alertRuleRepository.findByEnabledTrue();
    }

    @Transactional
    public AlertRule toggleRule(String ruleId, boolean enabled) {
        AlertRule rule = getRule(ruleId);
        rule.setEnabled(enabled);

        AlertRule saved = alertRuleRepository.save(rule);

        log.info("Alert rule {} {}", ruleId, enabled ? "enabled" : "disabled");

        return saved;
    }
}