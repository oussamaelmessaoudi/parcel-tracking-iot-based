package com.tracksecure.common.exception;

public class DeviceNotAvailableException extends RuntimeException{
    public DeviceNotAvailableException(String message){
        super(message);
    }

    public DeviceNotAvailableException(String message,Throwable cause){
        super(message,cause);
    }
}
