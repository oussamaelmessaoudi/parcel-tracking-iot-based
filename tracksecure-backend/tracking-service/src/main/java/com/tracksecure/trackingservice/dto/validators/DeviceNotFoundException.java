package com.tracksecure.trackingservice.dto.validators;

public class DeviceNotFoundException extends RuntimeException {
    public DeviceNotFoundException(String message) {
        super(message);
    }
    public DeviceNotFoundException(String deviceId, Throwable cause) {
        super("Device not found: "+ deviceId, cause);
    }
}
