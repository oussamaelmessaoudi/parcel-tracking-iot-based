package com.tracksecure.alertservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertDTO {

    @JsonProperty("alert_id")
    private String alertId;

    @JsonProperty("device_id")
    private String deviceId;

    @JsonProperty("rule_id")
    private String ruleId;

    @JsonProperty("timestamp")
    private Instant timestamp;

    @JsonProperty("alert_type")
    private String alertType;

    @JsonProperty("severity")
    private String severity;

    @JsonProperty("title")
    private String title;

    @JsonProperty("message")
    private String message;

    @JsonProperty("latitude")
    private Double latitude;

    @JsonProperty("longitude")
    private Double longitude;

    @JsonProperty("metadata")
    private Map<String, String> metadata;

    @JsonProperty("acknowledged")
    private Boolean acknowledged;

    @JsonProperty("acknowledged_at")
    private Instant acknowledgedAt;

    @JsonProperty("acknowledged_by")
    private String acknowledgedBy;

    @JsonProperty("created_at")
    private Instant createdAt;
}