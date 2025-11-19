package com.tracksecure.eventprocessorservice.consumer;


import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;

@Slf4j
@Configuration
@EnableKafkaStreams
public class EventStreamConsumer {

    @Value("${kafka.topic.tracking-events}")
    private String trackingEventsTopic;

    @Value("${kafka.topic.high-priority-events:high-priority-events}")
    private String highPriorityTopic;

    @Bean
    public KStream<String, byte[]> eventStream(StreamsBuilder streamsBuilder) {
        KStream<String, byte[]> stream = streamsBuilder.stream(
                trackingEventsTopic,
                Consumed.with(Serdes.String(), Serdes.ByteArray())
        );

        // Filter high-priority events (e.g., low battery, anomalies)
        stream
                .filter((key, value) -> isHighPriority(value))
                .to(highPriorityTopic, Produced.with(Serdes.String(), Serdes.ByteArray()));

        log.info("Kafka Streams configured for event processing");

        return stream;
    }

    private boolean isHighPriority(byte[] eventBytes) {
        try {
            com.tracksecure.proto.TrackingEventProto.TrackingEvent event =
                    com.tracksecure.proto.TrackingEventProto.TrackingEvent.parseFrom(eventBytes);

            // Check for low satellites
            if (event.getLocation().getSatellites() < 4) {
                return true;
            }

            return false;
        } catch (Exception e) {
            log.error("Error parsing event in stream: {}", e.getMessage());
            return false;
        }
    }
}