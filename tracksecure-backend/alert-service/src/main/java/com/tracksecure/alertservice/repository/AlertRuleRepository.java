package com.tracksecure.alertservice.repository;

import com.tracksecure.alertservice.model.AlertRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlertRuleRepository extends JpaRepository<AlertRule, Long> {

    Optional<AlertRule> findByRuleId(String ruleId);

    List<AlertRule> findByDeviceIdAndEnabledTrue(String deviceId);

    List<AlertRule> findByEnabledTrue();

    List<AlertRule> findByDeviceId(String deviceId);

    List<AlertRule> findByRuleType(AlertRule.RuleType ruleType);

    boolean existsByDeviceIdAndRuleTypeAndEnabledTrue(String deviceId,
                                                      AlertRule.RuleType ruleType);
}