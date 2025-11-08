package com.tracksecure.iotgatewayservice.service;

import com.tracksecure.iotgatewayservice.kafka.KafkaProducer;
import com.tracksecure.iotgatewayservice.model.DeviceMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.tracksecure.proto.TrackingEventProto;

import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class MessageTransformService{
    private final KafkaProducer kafkaProducer;
    private final GatewayService gatewayService;

    @Async
    public void processAndPublish(DeviceMessage message){
        try{
            gatewayService.validateMessage(message);
            gatewayService.recordDeviceActivity(message.getDeviceId());
            TrackingEventProto.TrackingEvent trackingEvent = transformToProto(message);
            kafkaProducer.sendTrackingEvent(trackingEvent);

            log.debug("Successfully processed and published message for device: {}",
                    message.getDeviceId());
        }catch (Exception e){
            log.error("Failed to process device message {}: {}",message.getDeviceId(),e);
        }
    }

    private TrackingEventProto.TrackingEvent transformToProto(DeviceMessage message) {
        TrackingEventProto.LocationData.Builder locationBuilder =
                TrackingEventProto.LocationData.newBuilder()
                        .setLatitude(message.getLocation().getLatitude())
                        .setLongitude(message.getLocation().getLongitude());

        TrackingEventProto.TelemetryData.Builder telemetryBuilder =
                TrackingEventProto.TelemetryData.newBuilder()
                        .setTemperature(message.getTelemetry().getTemperature())
                        .setHumidity(message.getTelemetry().getHumidity());

        if (message.getAdditionalData() != null) {
            telemetryBuilder.putAllAdditionalData(message.getAdditionalData());
        }

        return TrackingEventProto.TrackingEvent.newBuilder()
                .setDeviceId(message.getDeviceId())
                .setEventId(UUID.randomUUID().toString())
                .setTimestamp(message.getTimestamp())
                .setLocation(locationBuilder.build())
                .setTelemetry(telemetryBuilder.build())
                .setEventType(message.getEventType() != null ? message.getEventType() : "LOCATION_UPDATE")
                .build();
    }
}
