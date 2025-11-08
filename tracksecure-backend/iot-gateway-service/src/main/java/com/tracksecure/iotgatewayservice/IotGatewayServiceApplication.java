package com.tracksecure.iotgatewayservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableIntegration
@EnableKafka
@EnableAsync
public class IotGatewayServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(IotGatewayServiceApplication.class, args);
    }

}
