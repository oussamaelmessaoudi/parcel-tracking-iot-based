package com.tracksecure.sensorspayload.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;


@ConfigurationProperties(prefix = "mqtt")
@Getter
@Setter
public class MqttProperties {
    private String broker;
    private String clientId;
    private String username;
    private String password;
    private String caCertPath;
    private List<String> topics;
}
