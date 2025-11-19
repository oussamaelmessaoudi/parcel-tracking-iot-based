package com.tracksecure.eventprocessorservice.service;

import com.tracksecure.eventprocessorservice.kafka.KafkaProducer;
import com.tracksecure.eventprocessorservice.model.ProcessedEvent;
import com.tracksecure.proto.ProcessedEventProto;
import com.tracksecure.proto.TrackingEventProto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventProcessingService {
    private final EventValidationService validationService;
    private final EventEnrichmentService enrichmentService;
    private final KafkaProducer kafkaProducer;

    private final AtomicLong processedCount = new AtomicLong(0);
    private final AtomicLong validCount = new AtomicLong(0);
    private final AtomicLong invalidCount = new AtomicLong(0);
    private final AtomicLong anomalyCount = new AtomicLong(0);

    public void processEvent(TrackingEventProto.TrackingEvent event) {
        try {
            long startTime = System.currentTimeMillis();

            log.debug("Processing event {} for device {}",
                    event.getEventId(), event.getDeviceId());

            // Step 1: Validate
            ProcessedEvent.ValidationInfo validation =
                    validationService.validate(event);

            // Step 2: Enrich
            ProcessedEvent.EnrichmentInfo enrichment =
                    enrichmentService.enrich(event);

            // Step 3: Build DTO
            ProcessedEvent processedEvent =
                    buildProcessedEvent(event, validation, enrichment);

            // Step 4: Status
            ProcessedEvent.EventStatus status =
                    determineEventStatus(validation, enrichment);

            processedEvent.setStatus(status);

            // Step 5: Convert to proto
            ProcessedEventProto.ProcessedEvent proto =
                    convertToProto(processedEvent);

            // Step 6: Publish
            publishEvent(proto, status);

            // Metrics
            updateMetrics(status, enrichment);

            long time = System.currentTimeMillis() - startTime;

            log.info("Processed event {} for device {} in {}ms - Status: {}",
                    event.getEventId(), event.getDeviceId(), time, status);

        } catch (Exception e) {
            log.error("Error processing event {} for device {}: {}",
                    event.getEventId(), event.getDeviceId(),
                    e.getMessage(), e);
        }
    }

    private ProcessedEvent buildProcessedEvent(TrackingEventProto.TrackingEvent event,
                                               ProcessedEvent.ValidationInfo validation,
                                               ProcessedEvent.EnrichmentInfo enrichment) {

        // Convert location data
        ProcessedEvent.LocationInfo location = ProcessedEvent.LocationInfo.builder()
                .latitude(event.getLocation().getLatitude())
                .longitude(event.getLocation().getLongitude())
                .satellites(event.getLocation().getSatellites())
                .build();

        // Convert telemetry data
        ProcessedEvent.TelemetryInfo telemetry = ProcessedEvent.TelemetryInfo.builder()
                .temperature(event.getTelemetry().getTemperature())
                .humidity(event.getTelemetry().getHumidity())
                .additionalData(event.getTelemetry().getAdditionalDataMap())
                .build();

        return ProcessedEvent.builder()
                .eventId(event.getEventId())
                .deviceId(event.getDeviceId())
                .timestamp(Instant.ofEpochMilli(event.getTimestamp()))
                .processingTimestamp(Instant.now())
                .location(location)
                .telemetry(telemetry)
                .enrichment(enrichment)
                .validation(validation)
                .eventType(event.getEventType())
                .build();
    }

    private ProcessedEvent.EventStatus determineEventStatus(
            ProcessedEvent.ValidationInfo validation,
            ProcessedEvent.EnrichmentInfo enrichment) {

        if (!validation.getIsValid()) {
            return ProcessedEvent.EventStatus.INVALID;
        }

        if (enrichment.getIsAnomaly() != null && enrichment.getIsAnomaly()) {
            return ProcessedEvent.EventStatus.SUSPICIOUS;
        }

        if (validation.getConfidenceScore() < 70.0f) {
            return ProcessedEvent.EventStatus.SUSPICIOUS;
        }

        return ProcessedEvent.EventStatus.ENRICHED;
    }

    private ProcessedEventProto.ProcessedEvent convertToProto(ProcessedEvent event) {
        // Convert location
        ProcessedEventProto.LocationData.Builder locationBuilder =
                ProcessedEventProto.LocationData.newBuilder()
                        .setLatitude(event.getLocation().getLatitude())
                        .setLongitude(event.getLocation().getLongitude())
                        .setSatellites(event.getLocation().getSatellites());

        if (event.getLocation().getCountry() != null) {
            locationBuilder.setCountry(event.getLocation().getCountry());
        }
        if (event.getLocation().getCity() != null) {
            locationBuilder.setCity(event.getLocation().getCity());
        }

        // Convert telemetry
        ProcessedEventProto.TelemetryData.Builder telemetryBuilder =
                ProcessedEventProto.TelemetryData.newBuilder()
                        .setTemperature(event.getTelemetry().getTemperature())
                        .setHumidity(event.getTelemetry().getHumidity());

        if (event.getTelemetry().getAdditionalData() != null) {
            telemetryBuilder.putAllAdditionalData(event.getTelemetry().getAdditionalData());
        }

        // Convert enrichment
        ProcessedEventProto.EnrichedData.Builder enrichmentBuilder =
                ProcessedEventProto.EnrichedData.newBuilder()
                        .setIsMoving(event.getEnrichment().getIsMoving())
                        .setIsStationary(event.getEnrichment().getIsStationary())
                        .setMotionState(event.getEnrichment().getMotionState())
                        .setIsAnomaly(event.getEnrichment().getIsAnomaly());

        if (event.getEnrichment().getDistanceFromPrevious() != null) {
            enrichmentBuilder.setDistanceFromPrevious(event.getEnrichment().getDistanceFromPrevious());
        }
        if (event.getEnrichment().getTimeSincePrevious() != null) {
            enrichmentBuilder.setTimeSincePrevious(event.getEnrichment().getTimeSincePrevious());
        }
        if (event.getEnrichment().getCalculatedSpeed() != null) {
            enrichmentBuilder.setCalculatedSpeed(event.getEnrichment().getCalculatedSpeed());
        }
        if (event.getEnrichment().getWeatherCondition() != null) {
            enrichmentBuilder.setWeatherCondition(event.getEnrichment().getWeatherCondition());
        }
        if (event.getEnrichment().getAnomalyReasons() != null) {
            enrichmentBuilder.addAllAnomalyReasons(event.getEnrichment().getAnomalyReasons());
        }

        // Convert validation
        ProcessedEventProto.ValidationResult.Builder validationBuilder =
                ProcessedEventProto.ValidationResult.newBuilder()
                        .setIsValid(event.getValidation().getIsValid())
                        .setConfidenceScore(event.getValidation().getConfidenceScore());

        if (event.getValidation().getValidationErrors() != null) {
            validationBuilder.addAllValidationErrors(event.getValidation().getValidationErrors());
        }
        if (event.getValidation().getWarnings() != null) {
            validationBuilder.addAllWarnings(event.getValidation().getWarnings());
        }

        // Build final processed event
        return ProcessedEventProto.ProcessedEvent.newBuilder()
                .setEventId(event.getEventId())
                .setDeviceId(event.getDeviceId())
                .setTimestamp(event.getTimestamp().toEpochMilli())
                .setProcessingTimestamp(event.getProcessingTimestamp().toEpochMilli())
                .setLocation(locationBuilder.build())
                .setTelemetry(telemetryBuilder.build())
                .setEnrichedData(enrichmentBuilder.build())
                .setValidation(validationBuilder.build())
                .setEventType(event.getEventType())
                .setStatus(convertEventStatus(event.getStatus()))
                .build();
    }

    private ProcessedEventProto.EventStatus convertEventStatus(
            ProcessedEvent.EventStatus status) {
        return switch (status) {
            case VALID -> ProcessedEventProto.EventStatus.VALID;
            case INVALID -> ProcessedEventProto.EventStatus.INVALID;
            case SUSPICIOUS -> ProcessedEventProto.EventStatus.SUSPICIOUS;
            case ENRICHED -> ProcessedEventProto.EventStatus.ENRICHED;
            default -> ProcessedEventProto.EventStatus.UNKNOWN;
        };
    }

    private void publishEvent(ProcessedEventProto.ProcessedEvent event,
                              ProcessedEvent.EventStatus status) {
        // Publish to main processed events topic
        kafkaProducer.sendProcessedEvent(event);

        // Publish to specific topics based on status
        switch (status) {
            case INVALID:
                kafkaProducer.sendInvalidEvent(event);
                break;
            case SUSPICIOUS:
                kafkaProducer.sendAnomalyEvent(event);
                break;
            case ENRICHED:
                kafkaProducer.sendEnrichedEvent(event);
                break;
        }
    }

    private void updateMetrics(ProcessedEvent.EventStatus status,
                               ProcessedEvent.EnrichmentInfo enrichment) {
        processedCount.incrementAndGet();

        switch (status) {
            case VALID, ENRICHED -> validCount.incrementAndGet();
            case INVALID -> invalidCount.incrementAndGet();
            case SUSPICIOUS -> {
                if (enrichment.getIsAnomaly() != null && enrichment.getIsAnomaly()) {
                    anomalyCount.incrementAndGet();
                }
            }
        }

        long count = processedCount.get();
        if (count % 100 == 0) {
            log.info("Processing metrics - Total: {}, Valid: {}, Invalid: {}, Anomalies: {}",
                    count, validCount.get(), invalidCount.get(), anomalyCount.get());
        }
    }

    public long getProcessedCount() {
        return processedCount.get();
    }

    public long getValidCount() {
        return validCount.get();
    }

    public long getInvalidCount() {
        return invalidCount.get();
    }

    public long getAnomalyCount() {
        return anomalyCount.get();
    }
}
