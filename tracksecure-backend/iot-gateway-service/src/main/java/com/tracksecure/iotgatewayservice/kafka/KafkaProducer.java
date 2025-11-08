package com.tracksecure.iotgatewayservice.kafka;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import com.tracksecure.proto.TrackingEventProto;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProducer {
    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    @Value("${kafka.topic.tracking-events}")
    private String trackingEventsTopic;

    public void sendTrackingEvent(TrackingEventProto.TrackingEvent event){
        try{
            byte[] eventBytes = event.toByteArray();

            CompletableFuture<SendResult<String,byte[]>> future =
                    kafkaTemplate.send(trackingEventsTopic, event.getDeviceId(),eventBytes);

            future.whenComplete((result,ex)->{
                if (ex == null) {
                    log.debug("Sent tracking event for device: {} to partition: {}",
                            event.getDeviceId(),
                            result.getRecordMetadata().partition());
                } else {
                    log.error("Failed to send tracking event for device: {}",
                            event.getDeviceId(), ex);
                }
            });
        }catch (Exception e){
            log.error("Failed to send tracking event to Kafka: {}",e.getMessage());
        }
    }
}
