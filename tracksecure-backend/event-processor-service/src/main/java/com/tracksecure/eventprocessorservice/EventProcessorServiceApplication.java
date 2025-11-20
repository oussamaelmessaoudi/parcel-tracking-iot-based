package com.tracksecure.eventprocessorservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableKafka
@EnableKafkaStreams
@EnableCaching
@EnableAsync
@EnableScheduling
public class EventProcessorServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventProcessorServiceApplication.class, args);
    }

}
