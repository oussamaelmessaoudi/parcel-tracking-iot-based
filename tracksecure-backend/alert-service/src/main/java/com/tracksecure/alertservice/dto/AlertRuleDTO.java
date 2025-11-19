package com.tracksecure.alertservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertRuleDTO {

    @JsonProperty("rule_id")
    private String ruleId;

    @NotBlank(message = "Rule name is required")
    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @NotBlank(message = "Device ID is required")
    @JsonProperty("device_id")
    private String deviceId;

    @NotNull(message = "Rule type is required")
    @JsonProperty("rule_type")
    private String ruleType;

    @NotNull(message = "Severity is required")
    @JsonProperty("severity")
    private String severity;

    @JsonProperty("conditions")
    private String conditions;

    @JsonProperty("enabled")
    private Boolean enabled;

    @JsonProperty("cooldown_minutes")
    private Integer cooldownMinutes;

    @JsonProperty("notification_channels")
    private String notificationChannels;

    @JsonProperty("created_at")
    private Instant createdAt;

    @JsonProperty("updated_at")
    private Instant updatedAt;
}
