package com.tracksecure.common.constants;

public final class KafkaTopics {
    private KafkaTopics(){
        throw new UnsupportedOperationException("Kafka Topics is utility class and cannot be instantiated");
    }

    public static final String TRACKING_EVENTS = "tracking-events";
    public static final String ALERTS = "alerts";
    public static final String DEVICE_STATUS = "device-status";
    public static final String SHIPMENT_UPDATES =  "shipment-updates";
    public static final String TELEMETRY_RAW = "telemetry-raw";


}
